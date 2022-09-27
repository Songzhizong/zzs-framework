package com.zzs.framework.core.trace;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/22
 */
public interface TraceConstants {
  @Nonnull
  String CTX_KEY = "_$ZZS$TRACE:CONTEXT#$$$$$$";

  @Nonnull
  String TRACE_ID_HEADER_NAME = "x-ideal-trace-id";

  @Nonnull
  String SPAN_ID_HEADER_NAME = "x-ideal-span-id";

}
