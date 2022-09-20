package com.zzs.framework.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author 宋志宗 on 2022/9/20
 */
public class IpUtilsTest {

  @Test
  public void ipv4ToInt() {
    assertEquals(IpUtils.ipv4ToInt("0.0.0.0"), Integer.MIN_VALUE);
    assertEquals(IpUtils.ipv4ToInt("255.255.255.255"), Integer.MAX_VALUE);
  }
}
