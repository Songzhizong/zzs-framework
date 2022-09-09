package com.zzs.framework.autoconfigure.id.snowflake;

import com.zzs.framework.autoconfigure.id.IdProperties;
import com.zzs.framework.core.id.IDGeneratorFactory;
import com.zzs.framework.core.id.snowflake.FixedSnowflakeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/14
 */
@ConditionalOnExpression("""
  '${zzs-id.type:snowflake}'.equalsIgnoreCase('snowflake')
  &&'${zzs-id.snowflake.factory:fixed}'.equalsIgnoreCase('fixed')
  """)
public class FixedSnowflakeAutoConfigure {
  private static final Log log = LogFactory.getLog(FixedSnowflakeAutoConfigure.class);

  @Bean
  public IDGeneratorFactory idGeneratorFactory(@Nonnull IdProperties properties) {
    log.info("use FixedSnowFlakeFactory");
    SnowflakeProperties snowflake = properties.getSnowflake();
    int dataCenterId = snowflake.getDataCenterId();
    int machineId = snowflake.getMachineId();
    return new FixedSnowflakeFactory(dataCenterId, machineId);
  }
}
