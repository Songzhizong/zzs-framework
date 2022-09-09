package com.zzs.framework.autoconfigure.cache;

import com.zzs.framework.core.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/13
 */
@ConfigurationProperties("zzs-cache")
public class CacheProperties {
  private static final String CONNECTOR = ":";
  @Nonnull
  private String prefix = "";

  private transient volatile String formattedPrefix = null;

  @Nonnull
  public String formattedPrefix() {
    if (formattedPrefix != null) {
      return formattedPrefix;
    }
    synchronized (this) {
      if (formattedPrefix != null) {
        return formattedPrefix;
      }
      String prefix = getPrefix();
      if (StringUtils.isBlank(prefix)) {
        formattedPrefix = "";
        return "";
      }
      if (prefix.endsWith(CONNECTOR)) {
        formattedPrefix = prefix;
        return prefix;
      }
      prefix = prefix + CONNECTOR;
      formattedPrefix = prefix;
      return prefix;
    }
  }

  @Nonnull
  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(@Nonnull String prefix) {
    this.prefix = prefix;
  }

}
