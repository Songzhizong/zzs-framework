package com.zzs.framework.core.cache.coroutine

import com.zzs.framework.core.cache.serialize.KeySerializer
import com.zzs.framework.core.cache.serialize.StringKeySerializer
import com.zzs.framework.core.cache.serialize.ValueSerializer
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import java.time.Duration
import kotlin.math.max

/**
 * @author 宋志宗 on 2022/8/15
 */
class RedisCacheBuilder<K : Any, V : Any>(
  private val prefix: String?,
  private val valueSerializer: ValueSerializer<V>,
  private val redisTemplate: ReactiveStringRedisTemplate
) {
  private var multiLevel = false
  private var memoryCacheSize: Long? = null
  private var memoryCacheTimeout: Duration? = null
  private var cacheNull = false
  private var nullTimeout: Duration? = null
  private var lock = false
  private var lockTimeout: Duration? = null
  private var timeoutSeconds = 2592000L
  private var maxTimeoutSeconds: Long? = null
  private var keySerializer: KeySerializer<K> = StringKeySerializer()

  fun keySerializer(keySerializer: KeySerializer<K>): RedisCacheBuilder<K, V> {
    this.keySerializer = keySerializer
    return this
  }

  fun cacheNull(timeout: Duration): RedisCacheBuilder<K, V> {
    this.cacheNull = true
    this.nullTimeout = timeout
    return this
  }

  fun multiLevel(
    size: Long = 1000L,
    timeout: Duration = Duration.ofSeconds(30)
  ): RedisCacheBuilder<K, V> {
    this.multiLevel = true
    this.memoryCacheSize = size
    this.memoryCacheTimeout = timeout
    return this
  }

  fun enableLock(timeout: Duration): RedisCacheBuilder<K, V> {
    this.lock = true
    this.lockTimeout = timeout
    return this
  }

  fun expireAfterWrite(expireAfterWrite: Duration): RedisCacheBuilder<K, V> {
    this.timeoutSeconds = max(expireAfterWrite.toSeconds(), 1)
    return this
  }

  fun expireAfterWrite(minTimeout: Duration, maxTimeout: Duration): RedisCacheBuilder<K, V> {
    this.timeoutSeconds = max(minTimeout.toSeconds(), 1)
    this.maxTimeoutSeconds = max(maxTimeout.toSeconds(), 1)
    return this
  }


  /**
   * 构建redis缓存
   *
   * @author 宋志宗 on 2022/8/15
   */
  fun build(namespace: String): RedisCache<K, V> {
    val redisPrefix = generateRedisPrefix(namespace)
    val directRedisCache = DirectRedisCache(
      redisPrefix, lock, cacheNull, keySerializer, valueSerializer,
      nullTimeout ?: Duration.ofSeconds(30),
      lockTimeout ?: Duration.ofSeconds(30),
      timeoutSeconds, maxTimeoutSeconds, redisTemplate
    )
    if (!multiLevel) {
      return directRedisCache
    }
    return MultiLevelRedisCache(
      memoryCacheSize ?: 1000,
      memoryCacheTimeout ?: Duration.ofSeconds(30),
      directRedisCache
    )
  }

  private fun generateRedisPrefix(namespace: String): String {
    val prefix = if (this.prefix.isNullOrBlank()) {
      ""
    } else if (this.prefix.endsWith(":")) {
      this.prefix
    } else {
      "${this.prefix}:"
    }
    return "$prefix$namespace"
  }
}
