package com.zzs.framework.core.spring;

import com.zzs.framework.core.trace.TraceContext;
import com.zzs.framework.core.trace.reactive.TraceContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/22
 */
public class TraceExchangeFilterFunction implements ExchangeFilterFunction {
  private static final ExchangeFilterFunction INSTANCE = new TraceExchangeFilterFunction();

  private TraceExchangeFilterFunction() {
  }

  @Nonnull
  public static ExchangeFilterFunction getInstance() {
    return INSTANCE;
  }

  @Nonnull
  @Override
  public Mono<ClientResponse> filter(@Nonnull ClientRequest request,
                                     @Nonnull ExchangeFunction next) {
    return TraceContextHolder.current()
      .flatMap(op -> {
        if (op.isPresent()) {
          TraceContext context = op.get();
          ClientRequest clientRequest = ClientRequest.from(request)
            .headers(headers -> WebClientTraceUtils.setTraceHeaders(headers, context))
            .build();
          return next.exchange(clientRequest);
        }
        return next.exchange(request);
      });
  }
}
