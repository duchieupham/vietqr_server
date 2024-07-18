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

public class DiscordUtil {
    private static final Logger logger = Logger.getLogger(DiscordUtil.class);

    public boolean sendMessageToDiscord(String message, String webhook) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("content", message);  // Discord yêu cầu trường "content" cho tin nhắn văn bản
                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(webhook).buildAndExpand();
                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder()
                        .baseUrl(uriComponents.toUriString())
                        .build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response.statusCode().is2xxSuccessful()) {
                    check = true;
                } else {
                    logger.error("DiscordUtil: sendMessageToDiscord: ERROR: " + response.statusCode().value());
                }
            }
        } catch (Exception e) {
            logger.error("DiscordUtil: sendMessageToDiscord: ERROR: " + e.getMessage() + System.currentTimeMillis());
        }
        return check;
    }
}
