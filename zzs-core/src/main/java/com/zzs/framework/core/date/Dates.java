package com.zzs.framework.core.date;

import com.zzs.framework.core.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 宋志宗 on 2021/6/3
 */
public class Dates {
  private static final Log log = LogFactory.getLog(Dates.class);

  /**
   * 存放不同的日期模板格式的sdf的Map ThreadLocal为每个线程创建一个SimpleDateFormat局部变量,因此不存在线程安全问题
   */
  private static final Map<String, ThreadLocal<SimpleDateFormat>> SDF_MAP = new ConcurrentHashMap<>();

  /**
   * 根据模式串从sdfMap中获取一个SimpleDateFormat实例
   *
   * @param pattern 模式串
   * @return SimpleDateFormat实例
   */
  private static SimpleDateFormat getSimpleDateFormat(String pattern) {
    ThreadLocal<SimpleDateFormat> threadLocal = SDF_MAP.computeIfAbsent(pattern, k -> {
      log.debug("put new SimpleDateFormat of pattern " + pattern + " to map");
      return ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
    });
    return threadLocal.get();
  }

  /**
   * Date转换为时间字符串
   *
   * @param date    Date
   * @param pattern 模式串 如<code>yyyy-MM-dd</code>
   * @return String 时间字符串 如<code>2017-01-01</code>
   */
  @Nullable
  public static String format(@Nullable Date date, @Nullable String pattern) {
    if (date == null || StringUtils.isBlank(pattern)) {
      log.info("参数校验失败,date或pattern 为空");
      return null;
    }
    return getSimpleDateFormat(pattern).format(date);
  }

  /**
   * 时间字符串转换为Date
   *
   * @param dateString 时间字符串 如<code>2018-06-07</code>
   * @param pattern    模式串 如<code>yyyy-MM-dd</code>
   * @return Date
   */
  @Nullable
  public static Date parse(@Nullable String dateString, @Nullable String pattern) {
    try {
      if (StringUtils.isBlank(dateString) || StringUtils.isBlank(pattern)) {
        log.info("参数校验失败,dateString或pattern 为空");
        return null;
      }
      return getSimpleDateFormat(pattern).parse(dateString);
    } catch (ParseException e) {
      log.error("e : ", e);
      return null;
    }
  }

  /**
   * 将时间戳转换为时间字符串
   *
   * @param timestamp 时间戳 如<code>1483200000000</code>
   * @param pattern   模式串 如<code>yyyy-MM-dd</code>
   * @return String 时间字符串 如<code>2017-01-01</code>
   */
  public static String format(long timestamp, String pattern) {
    return format(new Date(timestamp), pattern);
  }

  public static LocalDateTime toLocalDateTime(@Nonnull Date date) {
    Instant instant = date.toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    return instant.atZone(zoneId).toLocalDateTime();
  }

  private Dates() {
  }
}
