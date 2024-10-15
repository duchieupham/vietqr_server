package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class WebhookUtil {
    private static final Logger logger = Logger.getLogger(WebhookUtil.class);

    public boolean sendMessageToWebhook(String method, String webhook, String message) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("text", message);
                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(webhook).buildAndExpand();
                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();
                Mono<ClientResponse> responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response.statusCode().is2xxSuccessful()) {
                    check = true;
                }
            }
        } catch (Exception e) {
            logger.error("GoogleChatUtil: sendMessageToGoogleChat: ERROR: " + e.toString());
        }
        return check;
    }

    public void sendMessageToWebhookBitrix(String name, String lastName, String phone, String platform) {
        String webhook = EnvironmentUtil.getBitrixWebhook() + "?FIELDS[NAME]=" + name.trim() +
                "&FIELDS[LAST_NAME]=" + lastName.trim() + "&FIELDS[PHONE][0][VALUE]=" + phone + "&FIELDS[PHONE][0][VALUE_TYPE]=WORK&FIELDS[PLATFORM]=" + platform;
        try {
            // Build URL with PathVariable
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(webhook).buildAndExpand();
            // Create WebClient with authorization header
            WebClient webClient = WebClient.builder()
                    .baseUrl(uriComponents.toUriString())
                    .build();
            Mono<ClientResponse> responseMono = webClient.get()
                    .uri(uriComponents.toUri())
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                logger.info("sendMessageToWebhookBitrix: sendMessageToWebhookBitrix: SUCCESS " + phone);
            }
        } catch (Exception e) {
            logger.error("GoogleChatUtil: sendMessageToGoogleChat: ERROR: " + e.toString());
        }
    }
}
