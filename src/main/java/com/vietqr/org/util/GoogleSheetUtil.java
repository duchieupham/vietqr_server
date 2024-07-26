package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GoogleSheetUtil {
    public static final int WIDTH_PIXEL = 256;
    public static final String HEADER_INSERTED_FILE = "headerInserted.properties";
    public static final String STT_FILE = "sttCounter.properties";
    private static final Logger logger = Logger.getLogger(GoogleSheetUtil.class);
    public static GoogleSheetUtil instance;
    public Properties headerInsertedProperties = new Properties();
    public Map<String, Integer> sttCounterMap = new ConcurrentHashMap<>();

    public GoogleSheetUtil() {
        loadHeaderInsertedProperties();
        loadSttCounter();
    }

    public static synchronized GoogleSheetUtil getInstance() {
        if (instance == null) {
            instance = new GoogleSheetUtil();
        }
        return instance;
    }

    public static void shutdown() {
        if (instance != null) {
            instance.saveSttCounter();
        }
    }

    private void loadHeaderInsertedProperties() {
        try (InputStream input = new FileInputStream(HEADER_INSERTED_FILE)) {
            headerInsertedProperties.load(input);
        } catch (IOException ex) {
            logger.error("Error loading header inserted properties: " + ex.getMessage());
        }
    }

    private void saveHeaderInsertedProperties() {
        try (OutputStream output = new FileOutputStream(HEADER_INSERTED_FILE)) {
            headerInsertedProperties.store(output, null);
        } catch (IOException ex) {
            logger.error("Error saving header inserted properties: " + ex.getMessage());
        }
    }

    private void loadSttCounter() {
        try (InputStream input = new FileInputStream(STT_FILE)) {
            Properties sttProps = new Properties();
            sttProps.load(input);
            sttProps.forEach((key, value) -> sttCounterMap.put((String) key, Integer.parseInt((String) value)));
        } catch (IOException ex) {
            logger.error("Error loading STT counter properties: " + ex.getMessage());
        }
    }

    private void saveSttCounter() {
        try (OutputStream output = new FileOutputStream(STT_FILE)) {
            Properties sttProps = new Properties();
            sttCounterMap.forEach((key, value) -> sttProps.setProperty(key, String.valueOf(value)));
            sttProps.store(output, null);
        } catch (IOException ex) {
            logger.error("Error saving STT counter properties: " + ex.getMessage());
        }
    }

    private int getSttCounter(String webhook) {
        int newStt = sttCounterMap.compute(webhook, (key, val) -> (val == null) ? 1 : val + 1);
        saveSttCounter(); // Save the STT counter after updating
        return newStt;
    }

    public boolean insertHeader(String webhook) {
        try {
            if (headerInsertedProperties.containsKey(webhook) && Boolean.parseBoolean(headerInsertedProperties.getProperty(webhook))) {
                return true; // Tiêu đề đã được chèn
            }

            List<String> headers = Arrays.asList(
                    "STT", "Thời gian TT", "Số tiền (VND)", "Mã giao dịch", "Mã đơn hàng",
                    "Mã điểm bán", "Loại GD", "Thời gian tạo", "Tài khoản nhận", "Nội dung",
                    "Ghi chú", "Trạng thái"
            );

            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(webhook).buildAndExpand();

            WebClient webClient = WebClient.builder()
                    .baseUrl(uriComponents.toUriString())
                    .build();

            webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Collections.singletonMap("values", Collections.singletonList(headers))))
                    .exchange()
                    .block();

            headerInsertedProperties.setProperty(webhook, "true");
            saveHeaderInsertedProperties();

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
                LocalDateTime timePaid = convertLongToLocalDateTime(Long.parseLong(data.get("timePaid"))).plusHours(7);
                LocalDateTime timeCreated = convertLongToLocalDateTime(Long.parseLong(data.get("time"))).plusHours(7);

                String transType = data.get("transType").equals("C") ? "Giao dịch đến" : "Giao dịch đi";
                String status = getStatusTransaction(Integer.parseInt(data.get("status")));

                String amount = notificationContents.contains("AMOUNT") ?
                        StringUtil.formatNumberAsString(data.get("amount")) : "";

                List<String> rowData = Arrays.asList(
                        String.valueOf(getSttCounter(webhook)), // STT
                        formatLocalDateTime(timePaid), // Thời gian thanh toán
                        "'" + amount, // Số tiền (VND)
                        notificationContents.contains("REFERENCE_NUMBER") ? data.get("referenceNumber") : "-", // Mã giao dịch
                        !StringUtil.isNullOrEmpty(data.get("orderId")) ?
                                data.get("orderId") : "-", // Mã đơn hàng
                        !StringUtil.isNullOrEmpty(data.get("terminalName")) ?
                                data.get("terminalName") : "-",
                        transType, // Loại GD
                        formatLocalDateTime(timeCreated), // Thời gian tạo GD
                        data.get("bankAccount") + " - " + data.get("bankShortName"), // Tài khoản nhận
                        notificationContents.contains("CONTENT") ? data.get("content") : "-", // Nội dung
                        !StringUtil.isNullOrEmpty(data.get("note")) ?
                                data.get("note") : "-", // Ghi chú
                        status // Trạng thái
                );

                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(webhook).buildAndExpand();

                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();

                Map<String, Object> payload = new HashMap<>();
                payload.put("values", Collections.singletonList(rowData));
                //payload.put("position", "insertAtTop");

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

    private LocalDateTime convertLongToLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("GMT"));
    }

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
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(webhook + "/columns/" + columnIndex + "/width")
                    .buildAndExpand();

            WebClient webClient = WebClient.builder()
                    .baseUrl(uriComponents.toUriString())
                    .build();

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
