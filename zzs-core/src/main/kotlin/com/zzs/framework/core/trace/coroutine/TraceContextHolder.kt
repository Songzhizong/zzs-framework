package com.zzs.framework.core.trace.coroutine

import com.zzs.framework.core.trace.TraceContext
import com.zzs.framework.core.trace.reactive.TraceContextHolder
import kotlinx.coroutines.reactor.awaitSingle

/**
 * @author 宋志宗 on 2022/9/22
 */
object TraceContextHolder {

  suspend fun awaitContext(): TraceContext? {
    return TraceContextHolder.current().awaitSingle().orElse(null)
  }

  suspend fun awaitLogPrefix(): String {
    return awaitContext()?.logPrefix ?: ""
  }
}
