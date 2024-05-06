package com.vietqr.org.util;

import com.vietqr.org.dto.StartEndTimeDTO;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static final long A_DAY_TO_SECOND = 86400;
    private static final String GMT_PLUS_7 = "GMT+7";
    private static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String DateTimeFormatVN = "dd-MM-yyyy HH:mm:ss";

    private static final String DateTimeForMatRaw = "yyyyMMdd_HHmmss";

    private static final String DateFormat = "yyyy-MM-dd";
    // 7 * 60 * 60
    public static final long GMT_PLUS_7_OFFSET = 25200;

    public static long getCurrentDateTimeAsNumber() {
        ZoneId gmtPlus7 = ZoneId.of(GMT_PLUS_7);
        LocalDateTime localDateTime = LocalDateTime.now(gmtPlus7);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long getCurrentDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
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

    public static long plusMonthAsLongInt(long time, int month) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
                .plusMonths(month).plusDays(1).with(LocalTime.MAX);
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

    public static StartEndTimeDTO getStartEndCurrentDate() {
        StartEndTimeDTO startEndTimeDTO = new StartEndTimeDTO();
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        startEndTimeDTO.setStartTime(localDateTime.with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC));
        startEndTimeDTO.setEndTime(localDateTime.with(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC));
        return startEndTimeDTO;
    }

    public static long getPrevDate() {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        return localDateTime.minusDays(1).with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC);
    }

    public static String getCurrentDateAsString() {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        return localDateTime.with(LocalTime.MIN).format(DateTimeFormatter.ofPattern(DateFormat));
    }

    public static String getCurrentDateTimeAsString() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(GMT_PLUS_7));
        return localDateTime.format(DateTimeFormatter.ofPattern(DateTimeForMatRaw));
    }

    public static String getPrevDateAsString() {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        return localDateTime.with(LocalTime.MIN).minusDays(1).format(DateTimeFormatter.ofPattern(DateFormat));
    }

    public static String getPrevMonthAsString() {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        return localDateTime.minusMonths(1).with(LocalTime.MIN).format(DateTimeFormatter.ofPattern(DateFormat));
    }



    public static long getPrevMonth() {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of(GMT_PLUS_7)).toLocalDateTime();
        return localDateTime.minusMonths(1).with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC);
    }

    public static StartEndTimeDTO getStartEndTime(String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = new StartEndTimeDTO();
        LocalDateTime fromTime = LocalDateTime.parse(fromDate + " 00:00:00", DateTimeFormatter.ofPattern(DateTimeFormat));
        LocalDateTime toTime = LocalDateTime.parse(toDate + " 23:59:59", DateTimeFormatter.ofPattern(DateTimeFormat));
        startEndTimeDTO.setStartTime(fromTime.toEpochSecond(ZoneOffset.UTC));
        startEndTimeDTO.setEndTime(toTime.toEpochSecond(ZoneOffset.UTC));
        return startEndTimeDTO;
    }

    public static String removeTimeInDateTimeString(String fromDate) {
        return fromDate.substring(0, 10);
    }

    public static String getDateStringBaseLong(long time) {
        String result = "";
        if (time == 0) {
            result = "-";
        } else {
            time = time + GMT_PLUS_7_OFFSET;
            if (time > 0) {
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
                result = localDateTime.format(DateTimeFormatter.ofPattern(DateTimeFormatVN));
            }
        }
        return result;
    }

    public static long plusMinuteAsLongInt(LocalDateTime time, int minute) {
        LocalDateTime localDateTime = time.plusMinutes(minute);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
