package com.zzs.framework.core.utils;

import com.zzs.framework.core.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * @author 宋志宗 on 2022/9/20
 */
public final class IpUtils {
  private static final String IPV4_REGEX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
  private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

  private IpUtils() {
  }

  /**
   * <pre>
   *   IP地址换换成数字地址的方法如下：
   *   例子：219.239.110.138
   *   具体计算过程如下：
   *   219*2563+ 239*2562+110*2561+138*2560=3689901706
   *   219.239.110.138-->3689901706
   *   转换后的3689901706即为ip 219.239.110.138的数字地址
   * </pre>
   *
   * @param ipv4 ipv4地址值
   * @return 数字表现形式
   */
  public static int ipv4ToInt(@Nonnull String ipv4) {
    String[] split = StringUtils.split(ipv4, ".");
    //noinspection AlibabaUndefineMagicConstant
    if (split.length != 4) {
      throw new IllegalArgumentException("非法的ipv4地址: " + ipv4);
    }
    long r = 0;
    r += Long.parseLong(split[0]) << 24;
    r += Long.parseLong(split[1]) << 16;
    r += Long.parseLong(split[2]) << 8;
    r += Long.parseLong(split[3]);
    return Math.toIntExact(r + Integer.MIN_VALUE);
  }

  public static boolean isIpv4(@Nonnull String ipv4) {
    return true;
  }
}
