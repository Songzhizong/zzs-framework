package com.zzs.framework.core.trace.reactive;

import com.zzs.framework.core.exception.InternalServerException;
import com.zzs.framework.core.trace.TraceConstants;
import com.zzs.framework.core.trace.TraceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2022/9/22
 */
public final class TraceContextHolder {
  private static final Log log = LogFactory.getLog(TraceContextHolder.class);

  private TraceContextHolder() {
  }

  @Nonnull
  public static Mono<Optional<TraceContext>> current() {
    return Mono.deferContextual(ctx -> {
      boolean hasKey = ctx.hasKey(TraceConstants.CTX_KEY);
      if (!hasKey) {
        return Mono.just(Optional.empty());
      }
      Object o = ctx.get(TraceConstants.CTX_KEY);
      if (o instanceof TraceContext) {
        return Mono.just(Optional.of((TraceContext) o));
      }
      String className = o.getClass().getName();
      log.error("从上下文获取到的TraceContext值实际类型为: " + className);
      return Mono.error(new InternalServerException("value not instanceof TraceContext"));
    });
  }
}
