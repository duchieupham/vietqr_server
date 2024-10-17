package com.vietqr.org.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

public class LarkUtil {
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendMessageToLark(String message, String larkWebhookUrl) {
        boolean check = false;
        if (larkWebhookUrl != null && !larkWebhookUrl.trim().isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // String requestBody = "{\"content\":\"" + message + "\"}";
            String requestBody = "{\"msg_type\":\"text\",\"content\":{\"text\":\"" + message + "\"}}";

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(larkWebhookUrl, HttpMethod.POST,
                    requestEntity,
                    String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                //System.out.println("Tin nhắn đã được gửi đến Lark");
                check = true;
            } else {
                //System.out.println("Lỗi khi gửi tin nhắn đến Lark: " + responseEntity.getStatusCode());
            }
        }
        return check;
    }
}
