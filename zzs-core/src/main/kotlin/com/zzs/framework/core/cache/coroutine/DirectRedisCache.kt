package com.zzs.framework.core.cache.coroutine

import com.zzs.framework.core.cache.CacheUtils
import com.zzs.framework.core.cache.serialize.KeySerializer
import com.zzs.framework.core.cache.serialize.ValueSerializer
import com.zzs.framework.core.spring.RedisTemplateUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import java.time.Duration
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * @author 宋志宗 on 2022/8/15
 */
class DirectRedisCache<K : Any, V : Any>(
  redisPrefix: String,
  private val lock: Boolean,
  private val cacheNull: Boolean,
  internal val keySerializer: KeySerializer<K>,
  private val valueSerializer: ValueSerializer<V>,
  private val nullTimeout: Duration,
  private val lockTimeout: Duration,
  private val timeoutSeconds: Long,
  private val maxTimeoutSeconds: Long?,
  private val redisTemplate: ReactiveStringRedisTemplate
) : RedisCache<K, V> {
  private val uuid = UUID.randomUUID().toString().replace("-", "")
  private val timeout = Duration.ofSeconds(timeoutSeconds)
  private val finalPrefix: String

  init {
    finalPrefix = if (redisPrefix.isBlank()) {
      ""
    } else if (redisPrefix.endsWith(":")) {
      redisPrefix
    } else {
      "$redisPrefix:"
    }
  }

  override suspend fun getIfPresent(key: K): V? {
    val redisKey = redisKey(keySerializer.serialize(key))
    val value = redisTemplate.opsForValue().get(redisKey).awaitSingleOrNull()
    if (CacheUtils.isNull(value)) {
      return null
    }
    return valueSerializer.deserialize(value!!)
  }

  override suspend fun get(key: K, block: suspend (K) -> V?): V? {
    val redisKey = redisKey(keySerializer.serialize(key))
    val ops = redisTemplate.opsForValue()
    val value = ops.get(redisKey).awaitSingleOrNull()
    if (!CacheUtils.isNull(value)) {
      return valueSerializer.deserialize(value!!)
    }
    var lockKey: String? = null
    try {
      if (lock) {
        lockKey = lockKey(redisKey)
        val tryLock = ops.setIfAbsent(lockKey, uuid, lockTimeout)
          .awaitSingleOrNull() ?: false
        if (!tryLock) {
          return null
        }
      }
      val invoke = block.invoke(key)
      if (invoke != null) {
        val serialize = valueSerializer.serialize(invoke)
        val timeout = calculateTimeout()
        ops.set(redisKey, serialize, timeout).awaitSingleOrNull()
      } else if (cacheNull) {
        val nullValue = CacheUtils.NULL_VALUE
        ops.set(redisKey, nullValue, nullTimeout).awaitSingleOrNull()
      }
      return invoke
    } finally {
      if (lockKey != null) {
        RedisTemplateUtils.unlock(redisTemplate, lockKey, uuid).awaitSingleOrNull()
      }
    }
  }

  override suspend fun put(key: K, v: V) {
    val redisKey = redisKey(keySerializer.serialize(key))
    val serialize = valueSerializer.serialize(v)
    val timeout = calculateTimeout()
    redisTemplate.opsForValue().set(redisKey, serialize, timeout).awaitSingleOrNull()
  }

  override suspend fun putAll(map: Map<K, V>) {
    coroutineScope {
      map.map { (k, v) -> async { put(k, v) } }.forEach { it.await() }
    }
  }

  override suspend fun invalidate(key: K) {
    val redisKey = redisKey(keySerializer.serialize(key))
    redisTemplate.opsForValue().delete(redisKey).awaitSingleOrNull()
  }

  override suspend fun invalidateAll(keys: Iterable<K>) {
    coroutineScope {
      keys.map { key -> async { invalidate(key) } }.forEach { it.await() }
    }
  }

  private fun redisKey(key: String) = "$finalPrefix$key"

  private fun lockKey(redisKey: String): String = "lock_key:$redisKey"

  private fun calculateTimeout(): Duration {
    return if (maxTimeoutSeconds == null || maxTimeoutSeconds < timeoutSeconds) {
      timeout
    } else {
      val random = ThreadLocalRandom.current().nextLong(timeoutSeconds, maxTimeoutSeconds)
      Duration.ofSeconds(random)
    }
  }
}
