package com.zzs.framework.autoconfigure.event.reactive;

import com.zzs.framework.autoconfigure.event.properties.EventProperties;
import com.zzs.framework.starter.model.event.reactive.ReactiveEventModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.Sender;

/**
 * @author 宋志宗 on 2022/8/13
 */
@ConditionalOnClass(ReactiveEventModel.class)
public class ReactiveEventInitializing implements SmartInitializingSingleton {
  private static final Log log = LogFactory.getLog(ReactiveEventInitializing.class);
  private final Sender sender;
  private final EventProperties properties;

  public ReactiveEventInitializing(Sender sender, EventProperties properties) {
    this.sender = sender;
    this.properties = properties;
  }

  @Override
  public void afterSingletonsInstantiated() {
    if (sender == null || properties == null) {
      return;
    }
    String exchange = properties.getRabbit().getExchange();
    ExchangeSpecification topic = ExchangeSpecification
      .exchange(exchange).type("topic").durable(true);
    sender.declareExchange(topic).block();
    log.info("declare exchange: " + exchange);
  }
}
