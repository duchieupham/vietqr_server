package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GoogleSheetUtil {
    private static final Logger logger = Logger.getLogger(GoogleSheetUtil.class);

    private static GoogleSheetUtil instance;
    public boolean headerInserted  = false;
    private int sttCounter = 1; // Khai báo và khởi tạo biến sttCounter
    private static final int WIDTH_PIXEL = 256;

    private GoogleSheetUtil() {

    }

    public static synchronized GoogleSheetUtil getInstance() {
        if (instance == null) {
            instance = new GoogleSheetUtil();
        }
        return instance;
    }

    // CÁch 6
    public boolean insertHeader(String webhook) {
        try {
            // Định nghĩa tiêu đề các cột giống với file Excel
            List<String> headers = Arrays.asList(
                    "STT", "Thời gian thanh toán", "Số tiền (VND)", "Mã giao dịch", "Mã đơn hàng",
                    "Mã điểm bán", "Loại GD", "Thời gian tạo GD", "Tài khoản nhận", "Nội dung",
                    "Ghi chú", "Trạng thái"
            );

            // Xây dựng URL với PathVariable
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(webhook).buildAndExpand();

            // Tạo WebClient với header authorization
            WebClient webClient = WebClient.builder()
                    .baseUrl(uriComponents.toUriString())
                    .build();

            // Chèn tiêu đề
            webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Collections.singletonMap("values", Collections.singletonList(headers))))
                    .exchange()
                    .block();
            headerInserted = true;

            // Đặt kích thước cột
            for (int i = 0; i < headers.size(); i++) {
                setColumnWidth(webhook, i, getColumnWidth(i));
            }

            return true;
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: insertHeader: ERROR: " + e.toString());
            return false;
        }
    }

    public boolean insertTransactionToGoogleSheet(Map<String, String> data, List<String> notificationContents, String webhook) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                LocalDateTime timePaid = convertLongToLocalDateTime(Long.parseLong(data.get("timePaid")));
                LocalDateTime timeCreated = convertLongToLocalDateTime(Long.parseLong(data.get("time")));

                // Định nghĩa giá trị của "Loại GD"
                String transType = data.get("transType").equals("C") ? "Giao dịch đến" : "Giao dịch đi";
                // Định nghĩa giá trị của "Trạng thái"
                String status = getStatusTransaction(Integer.parseInt(data.get("status")));

                // Định nghĩa giá trị của "Số tiền"
                String amount = notificationContents.contains("AMOUNT") ? data.get("amount") : "";

                // Tạo dữ liệu hàng từ đối tượng DTO theo thứ tự cột
                List<String> rowData = Arrays.asList(
                        String.valueOf(sttCounter++), // STT
                        formatLocalDateTime(timePaid), // Thời gian thanh toán
                        amount, // Số tiền (VND)
                        notificationContents.contains("REFERENCE_NUMBER") ? data.get("referenceNumber") : "", // Mã giao dịch
                        data.get("orderId"), // Mã đơn hàng
                        data.get("terminalName"), // Mã điểm bán
                        transType, // Loại GD
                        formatLocalDateTime(timeCreated), // Thời gian tạo GD
                        data.get("bankAccount") + " - " + data.get("bankName"), // Tài khoản nhận
                        notificationContents.contains("CONTENT") ? data.get("content") : "", // Nội dung
                        data.get("note"), // Ghi chú
                        status // Trạng thái
                );

                // Xây dựng URL với PathVariable
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(webhook).buildAndExpand();

                // Tạo WebClient với header authorization
                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();

                // Thêm một tham số mới để chỉ định chèn trên cùng
                Map<String, Object> payload = new HashMap<>();
                payload.put("values", Collections.singletonList(rowData));
                payload.put("position", "insertAtTop"); // Thêm chỉ dẫn chèn ở đầu

                webClient.post()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(payload))
                        .exchange()
                        .block();

                check = true;
            }
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: insertTransactionToGoogleSheet: ERROR: " + e.toString());
        }
        return check;
    }

    // Chuyển đổi Unix Epoch Seconds sang LocalDateTime với múi giờ GMT
    private LocalDateTime convertLongToLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("GMT"));
    }

    // Định dạng LocalDateTime thành chuỗi thời gian theo định dạng "dd/MM/yyyy HH:mm:ss"
    private String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    private String getStatusTransaction(int status) {
        switch (status) {
            case 0:
                return "Chờ thanh toán";
            case 1:
                return "Thành công";
            case 2:
                return "Đã hủy";
            default:
                return "-";
        }
    }

    private void setColumnWidth(String webhook, int columnIndex, int width) {
        try {
            // Xây dựng URL với PathVariable
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(webhook + "/columns/" + columnIndex + "/width")
                    .buildAndExpand();

            // Tạo WebClient với header authorization
            WebClient webClient = WebClient.builder()
                    .baseUrl(uriComponents.toUriString())
                    .build();

            // Đặt kích thước cột
            webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Collections.singletonMap("width", width)))
                    .exchange()
                    .block();
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: setColumnWidth: ERROR: " + e.toString());
        }
    }

    private int getColumnWidth(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return 9 * WIDTH_PIXEL; // STT
            case 1:
                return 22 * WIDTH_PIXEL; // Thời gian thanh toán
            case 2:
                return 14 * WIDTH_PIXEL; // Số tiền (VND)
            case 3:
                return 21 * WIDTH_PIXEL; // Mã giao dịch
            case 4:
                return 17 * WIDTH_PIXEL; // Mã đơn hàng
            case 5:
                return 17 * WIDTH_PIXEL; // Mã điểm bán
            case 6:
                return 20 * WIDTH_PIXEL; // Loại GD
            case 7:
                return 22 * WIDTH_PIXEL; // Thời gian tạo GD
            case 8:
                return 24 * WIDTH_PIXEL; // Tài khoản nhận
            case 9:
                return 40 * WIDTH_PIXEL; // Nội dung
            case 10:
                return 20 * WIDTH_PIXEL; // Ghi chú
            case 11:
                return 15 * WIDTH_PIXEL; // Trạng thái
            default:
                return 15 * WIDTH_PIXEL;
        }
    }
}
