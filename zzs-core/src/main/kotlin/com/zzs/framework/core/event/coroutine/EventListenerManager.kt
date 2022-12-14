package com.zzs.framework.core.event.coroutine

import com.zzs.framework.core.event.Event
import com.zzs.framework.core.event.EventListener
import kotlinx.coroutines.CoroutineScope

/**
 * 事件监听器管理器
 *
 * @author 宋志宗 on 2022/4/6
 */
interface EventListenerManager {

  /**
   * 监听事件
   *
   * @param queueName  监听器名称
   * @param topic 事件主题
   * @param clazz 事件类型
   * @param block 处理逻辑
   * @author 宋志宗 on 2022/4/8
   */
  fun <T : Event> listen(
    queueName: String,
    topic: String,
    clazz: Class<T>,
    block: suspend CoroutineScope.(T) -> Unit
  ): EventListener
}
