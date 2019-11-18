package com.lu.portable.detect.portableprint;

public class OtherUtil {
  public static void isNormal(int[] a) {
    if (a[0] <= 100) {
      a[0] = 100;
    } else if (a[0] >= 800) {
      a[0] = 800;
    }
    if (a[1] <= 50) {
      a[1] = 50;
    } else if (a[1] >= 200) {
      a[1] = 200;
    }
    if (a[2] <= 0) {
      a[2] = 0;
    } else if (a[2] >= 100) {
      a[2] = 100;
    }

    if (a[3] <= 25) {
      a[3] = 25;
    } else if (a[3] >= 65) {
      a[3] = 65;
    }
  }
}
