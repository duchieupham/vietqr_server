package com.vietqr.org.service;

import com.vietqr.org.controller.LarkController;
import com.vietqr.org.util.EnvironmentUtil;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutomationManagementService {
    private static final Logger logger = Logger.getLogger(LarkController.class);

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 30 8 * * ?")
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
                final String url = "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key=" + key
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

    // @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 19 09 * * ?")
    // public void myScheduledTask() {
    // // Your task logic goes here
    // System.out.println("System time is: " + LocalDateTime.now());
    // }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 30 17 * * ?")
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
                final String url = "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key=" + key
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
}
