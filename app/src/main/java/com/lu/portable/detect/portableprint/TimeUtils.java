package com.lu.portable.detect.portableprint;

public class TimeUtils {
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 5000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
