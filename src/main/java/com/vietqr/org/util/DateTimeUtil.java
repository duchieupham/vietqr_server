package com.vietqr.org.util;

import com.vietqr.org.dto.StartEndTimeDTO;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static final String GMT_PLUS_7 = "GMT+7";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String SIMPLE_DATE_FORMAT = "yyyyMMdd";
    // 7 * 60 * 60
    public static final long GMT_PLUS_7_OFFSET = 25200;

    public static long getCurrentDateTimeAsNumber() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId gmtPlus7 = ZoneId.of(GMT_PLUS_7);
        return localDateTime.atZone(gmtPlus7).toEpochSecond();
    }

    public static long getDateTimeAsLongInt(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long getDateAsLongInt(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.GMT_PLUS_7_OFFSET;
    }

    public static long get3MonthsPreviousAsLongInt() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(3).withDayOfMonth(1).with(LocalTime.MIN);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long get2LastPartition() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static StartEndTimeDTO getStartEndMonth(String month) {
        String dateTime = month + "-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                LocalDateTime fromDate = localDateTime
                .withDayOfMonth(1).with(LocalTime.MIN);
        YearMonth yearMonth = YearMonth.of(fromDate.getYear(), fromDate.getMonth());
        LocalDateTime toDate = localDateTime
                .withDayOfMonth(yearMonth.lengthOfMonth()).with(LocalTime.MAX);
        return new StartEndTimeDTO(fromDate.toEpochSecond(ZoneOffset.UTC),
                toDate.toEpochSecond(ZoneOffset.UTC));
    }

    public static StartEndTimeDTO getStartEndADate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String startDateString = date + " 00:00:00";
        String endDateString = date + " 23:59:59";
        LocalDateTime startDate = LocalDateTime.parse(startDateString, formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateString, formatter);
        return new StartEndTimeDTO(startDate.toEpochSecond(ZoneOffset.UTC),
                endDate.toEpochSecond(ZoneOffset.UTC));
    }

    public static String getDateString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate localDate = LocalDate.from(localDateTime);
        return localDate.format(formatter);
    }

    public static StartEndTimeDTO getStartEndDate(String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String startDateString = fromDate + " 00:00:00";
        String endDateString = toDate + " 23:59:59";
        LocalDateTime startDate = LocalDateTime.parse(startDateString, formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateString, formatter);
        return new StartEndTimeDTO(startDate.toEpochSecond(ZoneOffset.UTC),
                endDate.toEpochSecond(ZoneOffset.UTC));
    }

    public static String getSimpleDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
    }
}
