package com.vietqr.org.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateTimeUtil {
    private static final String GMT_PLUS_7 = "GMT+7";
    private static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    // 7 * 60 * 60
    public static final long GMT_PLUS_7_OFFSET = 25200;

    public static long getCurrentDateTimeAsNumber() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId gmtPlus7 = ZoneId.of(GMT_PLUS_7);
        return localDateTime.atZone(gmtPlus7).toEpochSecond();
    }

    public static long getDateTimeAsLongInt(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long get3MonthsPreviousAsLongInt() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(3).withDayOfMonth(1).with(LocalTime.MIN);
        ZoneId gmtPlus7 = ZoneId.of(GMT_PLUS_7);
        return localDateTime.atZone(gmtPlus7).toEpochSecond();
    }

    public static long get2LastPartition() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
