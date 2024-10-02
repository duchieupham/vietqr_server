package com.vietqr.org.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

public class GoogleChatUtil {
    private static final Logger logger = Logger.getLogger(GoogleChatUtil.class);

    public boolean sendMessageToGoogleChat(String message, String webhook) {
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

    public boolean sendMessageToGoogleChatInternal(String message) {
        boolean check = false;
        String webhook = "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk";
        try {
            if (webhook != null) {
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
}
