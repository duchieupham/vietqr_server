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

public class GoogleSheetUtil {
    private static final Logger logger = Logger.getLogger(GoogleSheetUtil.class);

    public boolean insertRowToGoogleSheet(String webhook, String[] rowData) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("values", new String[][]{ rowData });

                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();

                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response.statusCode().is2xxSuccessful()) {
                    check = true;
                }
            }
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: insertRowToGoogleSheet: ERROR: " + e.getMessage() + System.currentTimeMillis());
        }
        return check;
    }
}
