package com.vietqr.org.util;

import com.vietqr.org.dto.StartEndTimeDTO;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

public class DateTimeUtil {
    public static final long A_DAY_TO_SECOND = 86400;
    private static final String GMT_PLUS_7 = "GMT+7";
    private static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String DateTimeFormatVN = "dd-MM-yyyy HH:mm:ss";

    private static final String DateTimeForMatRaw = "yyyyMMdd_HHmmss";

    private static final String DateFormat = "yyyy-MM-dd";

    private static final String DateBIDVFormat = "yyMMdd";
    // 7 * 60 * 60
    public static final long GMT_PLUS_7_OFFSET = 25200;

    public static long getCurrentDateTimeAsNumber() {
        ZoneId gmtPlus7 = ZoneId.of(GMT_PLUS_7);
        LocalDateTime localDateTime = LocalDateTime.now(gmtPlus7);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long getCurrentDateTimeUTC() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long getDateTimeAsLongInt(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long get3MonthsPreviousAsLongInt() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(15).with(LocalTime.MIN);
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
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(15).with(LocalTime.MIN);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    //202405 => 2024-05
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
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(GMT_PLUS_7));
        startEndTimeDTO.setStartTime(localDateTime.with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC) - 25200);
        startEndTimeDTO.setEndTime(localDateTime.with(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC) - 25200);
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

    public static String removeFormatTime(String time) {
        String result = "";
        result = time.trim().replaceAll("\\-", "");
        result = result.trim().replaceAll("\\/", "");
        return result;
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

    public static String getFormatMonthYear(String time) {
        if (time == null || !time.matches("\\d{4}-\\d{2}")) {
            return "";
        }
        String[] parts = time.split("-");
        return parts[1] + "/" + parts[0];
    }

    public static StartEndTimeDTO getStartEndMonthV(String month) {
        try {
            String dateTime = month + "-01 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
            LocalDateTime fromDate = localDateTime.withDayOfMonth(1).with(LocalTime.MIN);
            YearMonth yearMonth = YearMonth.of(fromDate.getYear(), fromDate.getMonth());
            LocalDateTime toDate = localDateTime.withDayOfMonth(yearMonth.lengthOfMonth()).with(LocalTime.MAX);
            return new StartEndTimeDTO(fromDate.toEpochSecond(ZoneOffset.UTC),
                    toDate.toEpochSecond(ZoneOffset.UTC));
        } catch (DateTimeParseException e) {
            System.err.println("DateTimeParseException: Error parsing date: " + month);
            // Handle the error appropriately, perhaps logging it and returning null or a default value
            return null;
        }
    }

    public static int getDifferenceMonthFromTime(long fromDate, long toDate) {
        int result = 0;
        try {
            LocalDateTime fromDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDate), ZoneOffset.UTC);
            LocalDateTime toDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDate), ZoneOffset.UTC);
            result = (int) (toDateTime.getYear() - fromDateTime.getYear()) * 12 + toDateTime.getMonthValue() - fromDateTime.getMonthValue();
        } catch (Exception ignored) {
        }
        return result;
    }

    public static String getYearAsString(long toDate) {
        String result = "";
        try {
            LocalDateTime toDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDate), ZoneOffset.UTC);
            result = (toDateTime.getYear() + "").substring(2);
        } catch (Exception ignored) {
        }
        return result;
    }

    public static int getMonth(long toDate) {
        int result = 0;
        try {
            LocalDateTime toDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDate), ZoneOffset.UTC);
            result = toDateTime.getMonthValue();
        } catch (Exception ignored) {
        }
        return result;
    }

    public static long getEndTimeToDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime currentGMTPlus7 = localDateTime.plusHours(7);
        LocalDate current = currentGMTPlus7.toLocalDate();
        LocalDateTime endOfDate = LocalDateTime.of(current, LocalTime.MAX);
        return endOfDate.toEpochSecond(ZoneOffset.UTC);
    }

    public static String getDateStringFormat(String value) {
        String result = "";
        try {
            int year = Integer.parseInt(value.substring(0, 4));
            int month = Integer.parseInt(value.substring(4, 6));

            // Tạo chuỗi ngày tháng năm
            result = String.format("%04d-%02d", year, month);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static String getCurrentWeekYear() {
        String result = "";
        try {
            LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+7"));
            WeekFields weekFields = WeekFields.ISO;
            int weekYear = currentDate.get(weekFields.weekBasedYear());
            int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
            String weekYearTwoDigits = String.format("%02d", weekYear % 100);
            result = String.format("%s%02d", weekYearTwoDigits, weekNumber);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static long getMinusCurrentDate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC+7"));
        System.out.println(now.toEpochSecond(ZoneOffset.UTC));
        LocalDate currentDate = now.toLocalDate();
        WeekFields weekFields = WeekFields.ISO;
        DayOfWeek firstDayOfWeek = weekFields.getFirstDayOfWeek();

        // Tính ngày bắt đầu của tuần hiện tại
        LocalDate startOfWeek = currentDate.with(firstDayOfWeek);
        if (currentDate.getDayOfWeek().getValue() < firstDayOfWeek.getValue()) {
            startOfWeek = startOfWeek.minusWeeks(1);
        }
        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();

        // Tính chênh lệch thời gian theo giây
        Duration duration = Duration.between(startOfWeekDateTime, now);
        return duration.getSeconds();
    }

    public static String getBidvTranDate() {
        String result = "";
        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC+7"));
            LocalDate currentDate = now.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateBIDVFormat);
            result = currentDate.format(formatter);
        } catch (Exception e) {
        }
        return result;
    }

    public static long getStartDateUTCPlus7() {
        LocalDateTime toDateTime = LocalDateTime.now(ZoneOffset.UTC).plusHours(7);
        LocalDate startDate = toDateTime.toLocalDate();
        LocalDateTime startUtcPlus7 = startDate.atStartOfDay();

        // Chuyển thời điểm này về UTC
        ZonedDateTime startUtcPlus7Zoned = startUtcPlus7.atZone(ZoneOffset.ofHours(7));
        ZonedDateTime startUtc = startUtcPlus7Zoned.withZoneSameInstant(ZoneOffset.UTC);
        return startUtc.toEpochSecond();
    }

    public static long getDateTimeAsLongMMS(String datetime) {
        long result = DateTimeUtil.getCurrentDateTimeUTC();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
            ZoneId zoneIdUTC7 = ZoneId.of("Asia/Bangkok");
            ZonedDateTime utcPlus7DateTime = localDateTime.atZone(zoneIdUTC7);
            ZonedDateTime utcDateTime = utcPlus7DateTime.withZoneSameInstant(ZoneId.of("UTC"));
            result = utcDateTime.toEpochSecond();
        } catch (Exception e) {
            System.err.println("DateTimeParseException: Error parsing date: " + datetime);
        }

        return result;
    }

    public static long[] getCurrentAndSevenDaysLater() {
        long currentTime = (System.currentTimeMillis() - DateTimeUtil.GMT_PLUS_7_OFFSET) / 1000;
        long sevenDaysLater = currentTime + 7 * 24 * 60 * 60;
        return new long[]{currentTime, sevenDaysLater};
    }
}
