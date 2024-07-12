package com.vietqr.org.util;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleSheetUtil {
    private static final Logger logger = Logger.getLogger(GoogleSheetUtil.class);

//    public boolean insertRowToGoogleSheet(String webhook, String[] rowData) {
//        boolean check = false;
//        try {
//            if (webhook != null && !webhook.trim().isEmpty()) {
//                Map<String, Object> data = new HashMap<>();
//                data.put("values", new String[][]{ rowData });
//
//                // Build URL with PathVariable
//                UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();
//
//                // Create WebClient with authorization header
//                WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
//                Mono<ClientResponse> responseMono = webClient.post()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(BodyInserters.fromValue(data))
//                        .exchange();
//                ClientResponse response = responseMono.block();
//                if (response.statusCode().is2xxSuccessful()) {
//                    check = true;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("GoogleSheetUtil: insertRowToGoogleSheet: ERROR: " + e.getMessage() + System.currentTimeMillis());
//        }
//        return check;
//    }

    // Phương thức kiểm tra tiêu đề
    // Phương thức kiểm tra và chèn tiêu đề
    public boolean checkAndInsertHeader(String webhook, List<Object> headers) {
        boolean headerInserted = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("headers", Collections.singletonList(headers));

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
                    headerInserted = true;
                }
            }
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: checkAndInsertHeader: ERROR: " + e.getMessage() + System.currentTimeMillis());
        }
        return headerInserted;
    }

    // Phương thức chèn dữ liệu
    public boolean insertRowToGoogleSheet(String webhook, List<Object> rowData) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("values", Collections.singletonList(rowData));

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
