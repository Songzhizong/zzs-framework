package com.zzs.framework.autoconfigure.trace;

import com.zzs.framework.core.lang.StringUtils;
import com.zzs.framework.core.trace.reactive.OperationLogStore;
import com.zzs.framework.core.trace.reactive.OperatorHolder;
import com.zzs.framework.core.trace.reactive.TraceContextFilter;
import com.zzs.framework.starter.model.trace.reactive.ReactiveTraceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/9/22
 */
@ConditionalOnClass(ReactiveTraceModel.class)
public class ReactiveTraceAutoConfigure {

  @Value("${spring.application.name:}")
  private String applicationName;

  @Bean
  public TraceContextFilter traceContextFilter(@Nonnull TraceProperties properties,
                                               @Nullable @Autowired(required = false)
                                               OperatorHolder operatorHolder,
                                               @Nullable @Autowired(required = false)
                                               OperationLogStore operationLogStore,
                                               @Nonnull @Qualifier("requestMappingHandlerMapping")
                                               RequestMappingHandlerMapping handlerMapping) {
    String system = properties.getSystem();
    if (StringUtils.isBlank(system) && StringUtils.isNotBlank(applicationName)) {
      system = applicationName;
    }
    return new TraceContextFilter(system, operatorHolder, operationLogStore, handlerMapping);
  }
}
