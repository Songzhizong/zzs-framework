package com.zzs.framework.autoconfigure.event.reactive;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.zzs.framework.autoconfigure.cache.CacheProperties;
import com.zzs.framework.autoconfigure.event.properties.EventProperties;
import com.zzs.framework.autoconfigure.event.properties.EventRabbitProperties;
import com.zzs.framework.autoconfigure.event.properties.SpringRabbitProperties;
import com.zzs.framework.core.event.EventListenerManager;
import com.zzs.framework.core.event.ReactiveEventPublisher;
import com.zzs.framework.core.event.ReactiveTransactionalEventPublisher;
import com.zzs.framework.core.event.impl.RabbitEventListenerManager;
import com.zzs.framework.core.event.impl.ReactiveMongoTemplateTransactionalEventPublisher;
import com.zzs.framework.core.event.impl.ReactiveRabbitEventPublisher;
import com.zzs.framework.starter.model.event.reactive.ReactiveEventModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/13
 */
@ConditionalOnClass(ReactiveEventModel.class)
public class ReactiveEventAutoConfigure {

  @Bean
  public ConnectionFactory connectionFactory(@Nonnull SpringRabbitProperties rabbitProperties) {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.useNio();
    connectionFactory.setUsername(rabbitProperties.getUsername());
    connectionFactory.setPassword(rabbitProperties.getPassword());
    connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
    return connectionFactory;
  }

  @Bean
  public Sender sender(@Nonnull ConnectionFactory connectionFactory,
                       @Nonnull SpringRabbitProperties rabbitProperties) {
    Address[] addresses = rabbitProperties.getRabbitAddresses();
    SenderOptions senderOptions = new SenderOptions()
      .resourceManagementScheduler(Schedulers.boundedElastic())
      .connectionFactory(connectionFactory)
      .connectionSupplier(cf -> cf.newConnection(addresses, "monitor-sender"));
    return RabbitFlux.createSender(senderOptions);
  }

  @Bean
  public Receiver receiver(@Nonnull ConnectionFactory connectionFactory,
                           @Nonnull SpringRabbitProperties rabbitProperties) {
    Address[] addresses = rabbitProperties.getRabbitAddresses();
    ReceiverOptions receiverOptions = new ReceiverOptions()
      .connectionFactory(connectionFactory)
      .connectionSubscriptionScheduler(Schedulers.boundedElastic())
      .connectionSupplier(cf -> cf.newConnection(addresses, "monitor-receiver"));
    return RabbitFlux.createReceiver(receiverOptions);
  }

  @Bean
  public ReactiveEventPublisher reactiveEventPublisher(@Nonnull Sender sender,
                                                       @Nonnull EventProperties properties) {
    EventRabbitProperties rabbit = properties.getRabbit();
    String exchange = rabbit.getExchange();
    return new ReactiveRabbitEventPublisher(sender, exchange);
  }

  @Bean
  public ReactiveTransactionalEventPublisher reactiveTransactionalEventPublisher(
    @Nonnull ReactiveMongoTemplate template,
    @Nonnull ReactiveEventPublisher publisher
  ) {
    return new ReactiveMongoTemplateTransactionalEventPublisher(template, publisher);
  }

  @Bean
  public EventListenerManager eventListenerManager(@Nonnull CacheProperties cacheProperties,
                                                   @Nonnull EventProperties eventProperties,
                                                   @Nonnull Sender sender,
                                                   @Nonnull Receiver receiver,
                                                   @Nonnull ReactiveStringRedisTemplate redisTemplate) {
    EventRabbitProperties rabbit = eventProperties.getRabbit();
    String exchange = rabbit.getExchange();
    boolean temporary = rabbit.isTemporary();
    String queuePrefix = rabbit.getQueuePrefix();
    String cachePrefix = cacheProperties.formattedPrefix();
    return new RabbitEventListenerManager(
      exchange, temporary, queuePrefix, cachePrefix, sender, receiver, redisTemplate);
  }
}
