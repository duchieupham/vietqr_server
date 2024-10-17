package com.vietqr.org.service;

import com.vietqr.org.dto.UserScheduleInvoiceDTO;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AutomationManagementService {
    private static final Logger logger = Logger.getLogger(AutomationManagementService.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private FcmTokenService fcmTokenService;
    private boolean isFirstRun = true;

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 9 * * ?")
    public void scheduleExecuteTaskInvoice() {
        try {
            List<UserScheduleInvoiceDTO> dtos = invoiceService.getUserScheduleInvoice();

            for (UserScheduleInvoiceDTO item : dtos) {
                Thread thread2 = new Thread(() -> {
                    UUID notificationUUID = UUID.randomUUID();
                    String notiType = NotificationUtil.getNotiInvoiceCreated();
                    String title = NotificationUtil.getNotiTitleInvoiceUnpaid();
                    String message = "Bạn có "
                            + item.getNumberInvoice()
                            + " hoá đơn chưa thanh toán. Vui Lòng kiểm tra lại trên hệ thống VietQR VN.";

                    NotificationEntity notiEntity = new NotificationEntity();
                    notiEntity.setId(notificationUUID.toString());
                    notiEntity.setRead(false);
                    notiEntity.setMessage(message);
                    notiEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                    notiEntity.setType(notiType);
                    notiEntity.setUserId(item.getUserId());
                    notiEntity.setData(item.getUserId());
                    Map<String, String> datas = new HashMap<>();
                    datas.put("notificationType", notiType);
                    datas.put("notificationId", "");
                    datas.put("bankCode", "MB");
                    datas.put("terminalCode", "");
                    datas.put("terminalName", "");
                    datas.put("html", "<div><span style=\"font-size: 12;\">Bạn có "
                            + item.getNumberInvoice()
                            + " hóa đơn <br>cần thanh toán!</span></div>");
                    datas.put("invoiceId", "");  //invoice ID
                    datas.put("time", "0");
                    datas.put("invoiceName", "");
                    datas.put("status", 1 + "");
                    pushNotification(title, message, notiEntity, datas, notiEntity.getUserId());
                    logger.info("Start scheduled for invoice.");
                });
                thread2.start();
            }
        } catch (Exception e) {
            logger.error("AutomationManagementService: ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
        }
    }

    @Scheduled(fixedRate = 600000)
    public void scheduleExecuteTaskCheckPerformance() {
        if (isFirstRun) {
            logger.info("Skipping the first run of scheduleExecuteTaskCheckPerformance.");
            isFirstRun = false;
            return; // Bỏ qua lần đầu tiên
        }
        long time = 0;
        long timeResponse = 0;
        if (!EnvironmentUtil.isProduction()) {
            try {
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl("https://api.vietqr.org/vqr/status/performance")
                        .buildAndExpand(/* add url parameter here */);
                WebClient webClient = WebClient.builder()
                        .baseUrl("https://api.vietqr.org/vqr/status/performance")
                        .build();
                time = System.currentTimeMillis() + 25200000;
                Mono<ClientResponse> responseMono = webClient.get()
                        .uri(uriComponents.toUri())
                        .header("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwidXNlciI6IlkzVnpkRzl0W" +
                                "lhJdFltd3RkWE5sY2pBMSIsImlhdCI6MTcyNDk0NzQwOX0.Qy_YoRkSDNVdP4yMYeG_EINvE6ApzvmrBS7" +
                                "xWXkNkQcvagILbLn2xgG9fKXnVgJM94RPzCd64L5aARGIFyP24Q")
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response != null) {
                    timeResponse = System.currentTimeMillis() + 25200000;
                }
            } catch (Exception e) {
                logger.error("scheduleExecuteTaskCheckPerformance: ERROR: " + e.getMessage() +
                        " at: " + System.currentTimeMillis());
            } finally {
                if ((timeResponse - time) >= 10000) {
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
                    LocalDateTime responseTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeResponse), ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String content = "HEATH CHECK WARNING: " +
                            "\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F." +
                            "\n\nVUI LÒNG KIỂM TRA HEATH CHECK.\n\n" +
                            "TIME CALL: " + dateTime.format(formatter)
                            + "\n\nTIME RESPONSE: " + responseTime.format(formatter)
                            + "\n\nTIME DELAY: " + (timeResponse - time) + " ms";
                    GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                    googleChatUtil.sendMessageToGoogleChat(content, "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk");
                }
            }
        }
    }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 30 8 * * MON-FRI")
    public void scheduleExecuteTask() {
        if (!EnvironmentUtil.isProduction()) {
            String content = "CHÀO BUỔI SÁNG MỌI NGƯỜI " +
                    "\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F." +
                    "\n\nVUI LÒNG ĐIỂM DANH BẰNG CÁCH TRẢ LỜI TIN NHẮN NÀY.\n\n" +
                    "MỌI NGƯỜI HÃY CHECK LẠI TIẾN ĐỘ CÁC TASK ĐẦU NGÀY NHÉ!";
            String token = EnvironmentUtil.getGoogleChatToken();
            String key = EnvironmentUtil.getGoogleChatKey();
            try {
                // create JSON
                Map<String, Object> data = new HashMap<>();

                data.put("text", content);
                final String url = "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key="
                        + key
                        + "&token=" + token;
                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(url).buildAndExpand();
                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();
                //
                Mono<ClientResponse> responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                responseMono.block();
            } catch (Exception e) {
                logger.error("Error at scheduleExecuteTask: " + e.toString());
            }
        }
    }

    // @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 19 09 * * MON-FRI")
    // public void myScheduledTask() {
    // // Your task logic goes here
    // //System.out.println("System time is: " + LocalDateTime.now());
    // }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 30 17 * * MON-FRI")
    public void scheduleUpdateTask() {
        if (!EnvironmentUtil.isProduction()) {
            String content = "CUỐI NGÀY RỒI!!! " +
                    "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\n" +
                    "MỌI NGƯỜI CẬP NHẬT TRẠNG THÁI TASK CUỐI NGÀY.\n\n" +
                    "NHỚ COMMENT LẠI TIẾN ĐỘ CÔNG VIỆC VÀ KHÓ KHĂN KHI THỰC HIỆN TASK NHÉ.";
            String token = EnvironmentUtil.getGoogleChatToken();
            String key = EnvironmentUtil.getGoogleChatKey();
            try {
                // create JSON
                Map<String, Object> data = new HashMap<>();

                data.put("text", content);
                final String url = "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key="
                        + key
                        + "&token=" + token;
                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(url).buildAndExpand();
                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();
                //
                Mono<ClientResponse> responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                responseMono.block();
            } catch (Exception e) {
                logger.error("Error at scheduleUpdateTask: " + e.toString());
            }
        }
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId) {
        if (notiEntity != null) {
            notificationService.insertNotification(notiEntity);
        }

        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
        fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
        firebaseMessagingService.sendUsersNotificationWithData(data,
                fcmTokens,
                title, message);
        try {
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
                            + e.toString());
        }
    }
}
