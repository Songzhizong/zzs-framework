package com.zzs.framework.core.transmission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zzs.framework.core.json.JsonUtils;
import com.zzs.framework.core.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author 宋志宗 on 2021/8/25
 */
public class BasicResult implements Serializable {
  @Serial
  private static final long serialVersionUID = 1658084050565123764L;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String traceId = null;

  @Nullable
  private Boolean success = null;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String code = null;

  @Nonnull
  private String message = "success";

  public BasicResult() {
  }

  public static void main(String[] args) {
    BasicResult result = new BasicResult();
    result.setSuccess(true);
    System.out.println(JsonUtils.toJsonString(result));
  }

  @Transient
  public boolean isSucceed() {
    return Boolean.TRUE.equals(success);
  }

  public void setMessage(@Nullable String message) {
    if (StringUtils.isBlank(message)) {
      message = "";
    }
    this.message = message;
  }

  @Nullable
  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(@Nullable String traceId) {
    this.traceId = traceId;
  }

  @Nullable
  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(@Nullable Boolean success) {
    this.success = success;
  }

  @Nullable
  public String getCode() {
    return code;
  }

  public void setCode(@Nullable String code) {
    this.code = code;
  }

  @Nonnull
  public String getMessage() {
    return message;
  }

  @Transient
  public boolean isFailed() {
    return !isSucceed();
  }
}
