package com.zzs.framework.core.trace.reactive;

import com.zzs.framework.core.trace.OperationLog;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/23
 */
public interface OperationLogStore {

  @Nonnull
  Mono<Boolean> save(@Nonnull OperationLog operationLog);
}
