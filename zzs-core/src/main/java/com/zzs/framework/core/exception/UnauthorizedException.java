package com.zzs.framework.core.exception;

import javax.annotation.Nonnull;
import java.io.Serial;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class UnauthorizedException extends VisibleException {
  @Serial
  private static final long serialVersionUID = -6281199227602676282L;

  public UnauthorizedException() {
    super(401, null, "Unauthorized");
  }

  public UnauthorizedException(@Nonnull String message) {
    super(401, null, message);
  }
}
