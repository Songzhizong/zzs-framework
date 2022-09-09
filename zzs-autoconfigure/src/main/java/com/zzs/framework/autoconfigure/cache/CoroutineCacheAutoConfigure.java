package com.zzs.framework.autoconfigure.cache;

import com.zzs.framework.core.cache.coroutine.RedisCacheBuilderFactory;
import com.zzs.framework.starter.model.cache.coroutine.CoroutineCacheModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
@ConditionalOnClass(CoroutineCacheModel.class)
public class CoroutineCacheAutoConfigure {

  @Bean("coroutineRedisCacheBuilderFactory")
  public RedisCacheBuilderFactory redisCacheBuilderFactory(@Nonnull CacheProperties cacheProperties,
                                                           @Nonnull ReactiveStringRedisTemplate redisTemplate) {
    String prefix = cacheProperties.formattedPrefix();
    return new RedisCacheBuilderFactory(prefix, redisTemplate);
  }
}
