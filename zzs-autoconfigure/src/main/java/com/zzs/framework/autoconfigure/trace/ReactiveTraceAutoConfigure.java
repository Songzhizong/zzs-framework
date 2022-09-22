package com.zzs.framework.autoconfigure.trace;

import com.zzs.framework.core.trace.reactive.TraceContextFilter;
import com.zzs.framework.starter.model.trace.reactive.ReactiveTraceModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author 宋志宗 on 2022/9/22
 */
@ConditionalOnClass(ReactiveTraceModel.class)
public class ReactiveTraceAutoConfigure {

  @Bean
  public TraceContextFilter traceContextFilter() {
    return new TraceContextFilter();
  }
}
