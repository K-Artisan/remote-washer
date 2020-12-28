package com.azirtime.remote.common.utils;

public class TimeUtils {

    /**
     * 格式化时间为字符串 hh:mm
     * <p>
     * 示例：
     * 59分钟 -> 00:59
     * 1小时59分钟 -> 01:59
     */
    public static String stringForTimeHHMM(int timeSec) {

        int totalSeconds = timeSec;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
  /*      return hours > 0
                ? String.format("%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(hours), Integer.valueOf(seconds)})
                : String.format("%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(hours)});*/
        return String.format("%02d : %02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes)});
    }
}
