package com.zzs.framework.core.event.impl;

import com.rabbitmq.client.AMQP;
import com.zzs.framework.core.json.JsonUtils;
import com.zzs.framework.core.event.Event;
import com.zzs.framework.core.event.EventSupplier;
import com.zzs.framework.core.event.ReactiveEventPublisher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * @author 宋志宗 on 2022/4/2
 */
public class ReactiveRabbitEventPublisher implements ReactiveEventPublisher {
  private static final Log log = LogFactory.getLog(ReactiveRabbitEventPublisher.class);
  private final Sender sender;
  private final String exchange;

  public ReactiveRabbitEventPublisher(@Nonnull Sender sender, @Nonnull String exchange) {
    this.sender = sender;
    this.exchange = exchange;
  }

  @Nonnull
  @Override
  public Mono<Boolean> publish(@Nonnull Collection<EventSupplier> suppliers) {
    if (suppliers.isEmpty()) {
      return Mono.just(true);
    }
    Flux<OutboundMessage> messages = Flux.fromIterable(suppliers)
      .map(supplier -> {
        Event event = supplier.get();
        String topic = event.getTopic();
        String jsonString = JsonUtils.toJsonStringIgnoreNull(supplier);
        byte[] originalBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder().deliveryMode(2);
        AMQP.BasicProperties properties = builder.build();
        return new OutboundMessage(exchange, topic, properties, originalBytes);
      });

    return sender.sendWithPublishConfirms(messages).collectList().map(l -> true);
  }
}
