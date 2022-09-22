package com.zzs.framework.autoconfigure.trace;

import com.zzs.framework.core.id.IDGeneratorFactory;
import com.zzs.framework.core.trace.TraceIdGenerator;
import com.zzs.framework.core.trace.TraceIdGeneratorImpl;
import com.zzs.framework.core.trace.UUIDTraceIdGenerator;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/9/22
 */
public class TraceAutoConfigure implements SmartInitializingSingleton {
  @Nullable
  private final IDGeneratorFactory idGeneratorFactory;

  public TraceAutoConfigure(@Autowired(required = false) @Nullable
                            IDGeneratorFactory idGeneratorFactory) {
    this.idGeneratorFactory = idGeneratorFactory;
  }

  @Override
  public void afterSingletonsInstantiated() {
    if (idGeneratorFactory != null) {
      TraceIdGenerator generator = new TraceIdGeneratorImpl(idGeneratorFactory);
      TraceIdGenerator.Holder.set(generator);
    } else {
      TraceIdGenerator.Holder.set(UUIDTraceIdGenerator.INSTANCE);
    }
  }
}
