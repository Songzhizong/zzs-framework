package com.zzs.framework.core.trace;

import com.zzs.framework.core.id.IDGenerator;
import com.zzs.framework.core.id.IDGeneratorFactory;
import com.zzs.framework.core.utils.NumberSystemConverter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/22
 */
public class TraceIdGeneratorImpl implements TraceIdGenerator {
  private final IDGenerator generator;

  public TraceIdGeneratorImpl(@Nonnull IDGeneratorFactory snowflakeFactory) {
    this.generator = snowflakeFactory.getGenerator("traceId");
  }

  @Nonnull
  @Override
  public String generate() {
    long generate = generator.generate();
    return NumberSystemConverter.tenSystemTo(generate, NumberSystemConverter.SYSTEM_36);
  }
}
