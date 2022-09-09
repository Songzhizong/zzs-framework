package com.zzs.framework.core.cache.coroutine

/**
 * @author 宋志宗 on 2022/8/15
 */
interface RedisCache<K : Any, V : Any> {

  suspend fun getIfPresent(key: K): V?

  suspend fun get(key: K, block: suspend (K) -> V?): V?

  suspend fun put(key: K, v: V)

  suspend fun putAll(map: Map<K, V>)

  suspend fun invalidate(key: K)

  suspend fun invalidateAll(keys: Iterable<K>)
}
