package com.vietqr.org.util;

import com.vietqr.org.dto.StartEndTimeDTO;

import java.time.*;
import java.time.format.DateTimeFormatter;

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
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static StartEndTimeDTO get1MonthsPreviousAsLongInt() {
        StartEndTimeDTO startEndTimeDTO = new StartEndTimeDTO();
        LocalDateTime fromTime = LocalDateTime.now().minusMonths(1).with(LocalTime.MIN).atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        LocalDateTime toTime = LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        startEndTimeDTO.setStartTime(fromTime.toEpochSecond(ZoneOffset.UTC));
        startEndTimeDTO.setEndTime(toTime.toEpochSecond(ZoneOffset.UTC));
        return startEndTimeDTO;
    }

    public static long get2LastPartition() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static StartEndTimeDTO getStartEndMonth(String month) {
        String dateTime = month + "-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                LocalDateTime fromDate = localDateTime
                .withDayOfMonth(1).with(LocalTime.MIN);
        YearMonth yearMonth = YearMonth.of(fromDate.getYear(), fromDate.getMonth());
        LocalDateTime toDate = localDateTime
                .withDayOfMonth(yearMonth.lengthOfMonth()).with(LocalTime.MAX);
        return new StartEndTimeDTO(fromDate.toEpochSecond(ZoneOffset.UTC),
                toDate.toEpochSecond(ZoneOffset.UTC));
    }

    public static StartEndTimeDTO getStartEndDate(String date) {
        String dateTime = date + " 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime fromDate = localDateTime.with(LocalTime.MIN);
        LocalDateTime toDate = localDateTime.with(LocalTime.MAX);
        return new StartEndTimeDTO(fromDate.toEpochSecond(ZoneOffset.UTC),
                toDate.toEpochSecond(ZoneOffset.UTC));
    }

    public static StartEndTimeDTO getStartEndWeek() {
        StartEndTimeDTO startEndTimeDTO = new StartEndTimeDTO();
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        startEndTimeDTO.setStartTime(localDateTime.minusDays(7).with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC));
        startEndTimeDTO.setEndTime(localDateTime.with(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC));
        return startEndTimeDTO;
    }
}
