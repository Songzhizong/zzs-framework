package com.zzs.framework.autoconfigure.event.properties;

/**
 * @author 宋志宗 on 2022/8/13
 */
public class EventRabbitProperties {

  /** 交换区 */
  private String exchange = "event.exchange";

  /** 消费队列前缀 */
  private String queuePrefix = "event.queue";

  /** 启用此配置则为监听器创建随机名称的队列, 并在程序关闭时删除队列 */
  private boolean temporary = false;

  /** 预取消息数 */
  private int prefetchCount = 0;

  /** 消费者数 */
  private int consumers = 16;

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getQueuePrefix() {
    return queuePrefix;
  }

  public void setQueuePrefix(String queuePrefix) {
    this.queuePrefix = queuePrefix;
  }

  public boolean isTemporary() {
    return temporary;
  }

  public void setTemporary(boolean temporary) {
    this.temporary = temporary;
  }

  public int getPrefetchCount() {
    return prefetchCount;
  }

  public void setPrefetchCount(int prefetchCount) {
    this.prefetchCount = prefetchCount;
  }

  public int getConsumers() {
    return consumers;
  }

  public void setConsumers(int consumers) {
    this.consumers = consumers;
  }
}
