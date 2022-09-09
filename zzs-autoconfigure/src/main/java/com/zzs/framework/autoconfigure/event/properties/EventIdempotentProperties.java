package com.zzs.framework.autoconfigure.event.properties;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * @author 宋志宗 on 2022/8/13
 */
public class EventIdempotentProperties {
  @Nonnull
  private Duration timeout = Duration.ofHours(1);


  @Nonnull
  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(@Nonnull Duration timeout) {
    this.timeout = timeout;
  }
}
