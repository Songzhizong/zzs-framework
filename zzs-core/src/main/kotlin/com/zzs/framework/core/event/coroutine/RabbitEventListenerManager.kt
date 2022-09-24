package com.zzs.framework.core.event.coroutine

import com.zzs.framework.core.event.Event
import com.zzs.framework.core.event.EventListener
import com.zzs.framework.core.json.JsonUtils
import com.zzs.framework.core.spring.RedisTemplateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import reactor.core.Disposable
import reactor.rabbitmq.*
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * 事件监听器管理器
 *
 * @author 宋志宗 on 2022/4/2
 */
class RabbitEventListenerManager(
  private val exchange: String,
  private val temporary: Boolean,
  private val queuePrefix: String,
  private val cachePrefix: String,
  private val sender: Sender,
  private val receiver: Receiver,
  private val redisTemplate: ReactiveStringRedisTemplate,
) : EventListenerManager {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(RabbitEventListener::class.java)
    private val registry = ConcurrentHashMap<String, RabbitEventListener<*>>()
    private val timeout = Duration.ofMinutes(10)
  }

  init {
    val topic = ExchangeSpecification
      .exchange(exchange).type("topic").durable(true)
    sender.declareExchange(topic).block()
    log.info("declare exchange: $exchange")
  }


  @Suppress("UNCHECKED_CAST")
  override fun <T : Event> listen(
    queueName: String,
    topic: String,
    clazz: Class<T>,
    block: suspend CoroutineScope.(T) -> Unit
  ): RabbitEventListener<T> {
    var exist = true
    val eventListener = registry.computeIfAbsent(queueName) {
      log.info("register event listener: {}  ->  {}", queueName, topic)
      exist = false
      RabbitEventListener(
        exchange,
        topic,
        queueName,
        temporary,
        queuePrefix,
        cachePrefix,
        sender,
        receiver,
        redisTemplate,
        clazz, block
      )
    } as RabbitEventListener<T>
    if (exist) {
      val message = "监听器名称: $queueName 被重复注册"
      log.error(message)
      throw RuntimeException(message)
    }
    return eventListener
  }

  class RabbitEventListener<T : Event>(
    exchange: String,
    topic: String,
    queueName: String,
    temporary: Boolean,
    queuePrefix: String,
    private val cachePrefix: String,
    sender: Sender,
    private val receiver: Receiver,
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val clazz: Class<T>,
    private val block: suspend CoroutineScope.(T) -> Unit
  ) : EventListener {
    private val lockValue = UUID.randomUUID().toString()
    private val finalQueueName: String
    private var disposable: Disposable? = null


    init {
      val queue = if (temporary) {
        finalQueueName =
          queuePrefix + "." + queueName + "." + UUID.randomUUID().toString().replace("-", "")
        QueueSpecification.queue(finalQueueName).durable(false).exclusive(false).autoDelete(true)
      } else {
        finalQueueName = "$queuePrefix.$queueName"
        QueueSpecification.queue(finalQueueName).durable(true).exclusive(false).autoDelete(false)
      }
      sender.declareQueue(queue).block()
      sender.bind(BindingSpecification.binding(exchange, topic, finalQueueName)).block()
    }

    private fun start() {
      val options = ConsumeOptions()
      disposable = receiver.consumeManualAck(finalQueueName, options)
        .flatMap { delivery ->
          mono {
            var ack = true
            try {
              val body = delivery.body
              val string = String(body, Charsets.UTF_8)
              val message = try {
                JsonUtils.parse(string, clazz)
              } catch (e: Exception) {
                log.info("反序列化事件消息出现异常 {} ", clazz.name, e)
                return@mono
              }
              val uuid = message.uuid
              val key = "$cachePrefix$finalQueueName:$uuid"
              val tryLock = redisTemplate.opsForValue().setIfAbsent(key, lockValue, timeout)
                .awaitSingleOrNull()
              try {
                if (tryLock == true) {
                  block.invoke(this, message)
                }
              } catch (e: Exception) {
                ack = false
                try {
                  if (uuid.isNotBlank()) {
                    RedisTemplateUtils.unlock(redisTemplate, key, lockValue).awaitSingleOrNull()
                  }
                  log.warn("处理出现异常: ", e)
                  delay(1000)
                } catch (e: Exception) {
                  log.info("异常的后续处理出现异常: ", e)
                }
              }
            } finally {
              if (ack) {
                delivery.ack()
              } else {
                delivery.nack(true)
              }
            }
          }
        }.subscribe()
    }

    private fun stop() {
      if (disposable?.isDisposed == true) {
        return
      }
      disposable?.dispose()
    }

    override fun destroy() {
      this.stop()
    }

    override fun run(args: ApplicationArguments?) {
      this.start()
    }
  }
}
