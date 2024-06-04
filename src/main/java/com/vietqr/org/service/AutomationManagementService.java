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

        @Scheduled(fixedRate = 120000)
        public void scheduleExecuteTaskInvoice() {
                try {
                        List<UserScheduleInvoiceDTO> dtos = invoiceService.getUserScheduleInvoice();

                        for (UserScheduleInvoiceDTO item: dtos) {
                                Thread thread2 = new Thread(() -> {
                                        UUID notificationUUID = UUID.randomUUID();
                                        String notiType = NotificationUtil.getNotiInvoiceCreated();
                                        String title = NotificationUtil.getNotiTitleInvoiceUnpaid();
                                        String message = "Bạn có hoá đơn "
                                                + item.getNumberInvoice()
                                                + " chưa thanh toán. Vui Lòng kiểm tra lại trên hệ thống VietQR VN.";

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
                                        datas.put("html", "<div><span style=\"font-size: 12;\">Bạn có "+ item.getNumberInvoice()
                                                + " hóa đơn <br>cần thanh toán!</span></div>");
                                        datas.put("invoiceId", "");  //invoice ID
                                        datas.put("time", "0");
                                        datas.put("invoiceName", "");
                                        datas.put("status", 1 + "");
                                        pushNotification(title, message, notiEntity, datas, notiEntity.getUserId());
                                });
                                thread2.start();
                        }
                } catch (Exception e) {
                        logger.error("AutomationManagementService: ERROR: " + e.getMessage() +
                                " at: " + System.currentTimeMillis());
                }
        }

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
        // System.out.println("System time is: " + LocalDateTime.now());
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
