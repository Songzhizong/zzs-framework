package com.zzs.framework.core.trace.reactive;

import com.zzs.framework.core.lang.StringUtils;
import com.zzs.framework.core.trace.TraceConstants;
import com.zzs.framework.core.trace.TraceContext;
import com.zzs.framework.core.trace.TraceIdGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/22
 */
public class TraceContextFilter implements Ordered, WebFilter {
  private static final Log log = LogFactory.getLog(TraceContextFilter.class);

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Nonnull
  @Override
  public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    HttpHeaders headers = request.getHeaders();
    String traceId = headers.getFirst(TraceConstants.TRACE_ID_HEADER_NAME);
    String spanId = headers.getFirst(TraceConstants.SPAN_ID_HEADER_NAME);
    if (StringUtils.isNotBlank(traceId) && StringUtils.isBlank(spanId)) {
      log.warn("traceId不为空, 但是spanId为空");
    }
    TraceContext traceContext;
    if (StringUtils.isNotBlank(traceId) && StringUtils.isNotBlank(spanId)) {
      traceContext = new TraceContext(traceId, spanId);
    } else {
      traceId = TraceIdGenerator.Holder.get().generate();
      traceContext = new TraceContext(traceId);
    }
    TraceExchangeUtils.putTraceContext(exchange, traceContext);
    String logPrefix = traceContext.getLogPrefix();
    HttpMethod method = request.getMethod();
    String requestPath = request.getURI().getPath();
    log.info(logPrefix + method + " " + requestPath);
    return chain.filter(exchange)
      .contextWrite(ctx -> ctx.put(TraceConstants.CTX_KEY, traceContext))
      .doFinally(t -> {
        ServerHttpResponse response = exchange.getResponse();
        HttpStatus statusCode = response.getStatusCode();
        long survivalMillis = traceContext.getSurvivalMillis();
        if (statusCode == null) {
          log.info(logPrefix + method + " " + requestPath +
            " | consuming: " + survivalMillis + "ms");
        } else {
          int status = statusCode.value();
          String reasonPhrase = statusCode.getReasonPhrase();
          log.info(logPrefix + status + " " + reasonPhrase +
            " " + method + " " + requestPath + " | consuming: " + survivalMillis + "ms");
        }
      });
  }
}
