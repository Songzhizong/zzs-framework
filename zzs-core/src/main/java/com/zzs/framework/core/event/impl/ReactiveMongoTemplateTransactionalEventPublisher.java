package com.zzs.framework.core.event.impl;

import com.zzs.framework.core.event.*;
import com.zzs.framework.core.json.JsonUtils;
import com.zzs.framework.core.lang.CollectionUtils;
import com.zzs.framework.core.lang.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2022/4/1
 */
public class ReactiveMongoTemplateTransactionalEventPublisher
  implements ReactiveTransactionalEventPublisher, ApplicationRunner, DisposableBean {
  private static final Log log = LogFactory.getLog(ReactiveMongoTemplateTransactionalEventPublisher.class);
  private static final String LOCK_VALUE = UUID.randomUUID().toString();
  private final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
  @SuppressWarnings("AlibabaThreadPoolCreation")
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final ReactiveMongoTemplate reactiveMongoTemplate;
  private final ReactiveEventPublisher reactiveEventPublisher;

  public ReactiveMongoTemplateTransactionalEventPublisher(@Nonnull ReactiveMongoTemplate reactiveMongoTemplate,
                                                          @Nonnull ReactiveEventPublisher reactiveEventPublisher) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
    this.reactiveEventPublisher = reactiveEventPublisher;
  }

  @Nonnull
  @Override
  public Mono<Boolean> publish(@Nonnull Collection<EventSupplier> events) {
    if (CollectionUtils.isEmpty(events)) {
      return Mono.just(true);
    }
    long currentTimeMillis = System.currentTimeMillis();
    List<MongoEventTemp> collect = events.stream().map(s -> {
      Event event = s.get();
      String jsonString = JsonUtils.toJsonString(event);
      MongoEventTemp temp = new MongoEventTemp();
      temp.setEventInfo(jsonString);
      temp.setTimestamp(currentTimeMillis);
      return temp;
    }).collect(Collectors.toList());
    return reactiveMongoTemplate.insert(collect, MongoEventTemp.class).collectList().map(t -> true);
  }

  @Override
  public void run(ApplicationArguments args) {
    this.start();
  }

  @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
  private void start() {
    executor.execute(() -> {
      while (atomicBoolean.get()) {
        boolean executed = false;
        boolean sleep = true;
        try {
          try {
            MongoEventLock lock = MongoEventLock.create(LOCK_VALUE);
            reactiveMongoTemplate.insert(lock, MongoEventLock.DOCUMENT).block();
          } catch (Exception e) {
            continue;
          }
          executed = true;
          int limit = 500;
          Query query = new Query().limit(limit)
            .with(Sort.by(Sort.Order.asc("id")));
          List<MongoEventTemp> temps = reactiveMongoTemplate
            .findAllAndRemove(query, MongoEventTemp.class).collectList().block();
          if (Lists.isEmpty(temps)) {
            continue;
          }
          sleep = temps.size() < limit;
          List<EventSupplier> collect = temps.stream()
            .map(t -> JsonUtils.parse(t.getEventInfo(), GeneralEvent.class))
            .collect(Collectors.toList());
          CountDownLatch countDownLatch = new CountDownLatch(1);
          reactiveEventPublisher.publish(collect)
            .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
            .doFinally(f -> countDownLatch.countDown())
            .subscribe();
          countDownLatch.await();
        } catch (Exception e) {
          log.info("发布消息出现异常: ", e);
        } finally {
          try {
            if (executed) {
              Criteria criteria = Criteria.where("value").is(LOCK_VALUE);
              Query query = Query.query(criteria);
              reactiveMongoTemplate.remove(query, MongoEventLock.class).blockOptional()
                .ifPresent(res -> {
                  long deletedCount = res.getDeletedCount();
                  if (deletedCount == 0) {
                    log.warn("未能成功释放锁");
                  }
                });
            }
            if (sleep) {
              TimeUnit.SECONDS.sleep(1);
            }
          } catch (InterruptedException e) {
            // ignore
          }
        }
      }
    });
  }

  @Override
  public void destroy() {
    atomicBoolean.set(false);
    executor.shutdown();
  }
}
