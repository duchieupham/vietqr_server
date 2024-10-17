package com.vietqr.org.util.bank.bidv;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BIDVDateUtil {

    public static String getTransDate() {
        // Lấy thời gian hệ thống hiện tại
        LocalDate localDate = LocalDate.now();

        // Tạo đối tượng ZoneId với múi giờ UTC+7
        ZoneId zoneId = ZoneId.of("UTC+7");

        // Chuyển đổi thời gian hệ thống sang múi giờ UTC+7
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

        // Định dạng ngày theo yêu cầu (231128)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String formattedDate = zonedDateTime.format(formatter);

        return formattedDate;
    }

    public static String getSystemTimeWithOffset() {
        // Lấy thời gian hệ thống hiện tại
        LocalDateTime localDateTime = LocalDateTime.now();

        // Tạo đối tượng ZoneOffset với độ lệch múi giờ UTC+7
        ZoneOffset zoneOffset = ZoneOffset.ofHours(7);

        // Tạo đối tượng OffsetDateTime từ thời gian hệ thống và độ lệch múi giờ
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);

        // Định dạng thời gian theo yêu cầu (2020-01-31T09:59:34.000+07:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String formattedTime = offsetDateTime.format(formatter);
        //System.out.println("getSystemTimeWithOffset: formattedTime: " + formattedTime);
        return formattedTime;
    }
}
