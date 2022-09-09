package com.zzs.framework.autoconfigure.id.snowflake;

import com.zzs.framework.autoconfigure.id.IdProperties;
import com.zzs.framework.core.id.IDGeneratorFactory;
import com.zzs.framework.core.id.snowflake.SpringRedisSnowflakeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/14
 */
@ConditionalOnExpression("""
  '${zzs-id.type:snowflake}'.equalsIgnoreCase('snowflake')
  &&'${zzs-id.snowflake.factory:fixed}'.equalsIgnoreCase('redis')
  """)
public class RedisSnowflakeAutoConfigure {
  private static final Log log = LogFactory.getLog(RedisSnowflakeAutoConfigure.class);

  @Value("${spring.application.name:}")
  private String applicationName;

  @Bean
  public IDGeneratorFactory idGeneratorFactory(@Nonnull IdProperties properties,
                                               @Nonnull StringRedisTemplate stringRedisTemplate) {
    log.info("use SpringRedisSnowFlakeFactory");
    SnowflakeProperties snowflake = properties.getSnowflake();
    int dataCenterId = snowflake.getDataCenterId();
    return new SpringRedisSnowflakeFactory(dataCenterId, 600, 30, applicationName, stringRedisTemplate);
  }
}
