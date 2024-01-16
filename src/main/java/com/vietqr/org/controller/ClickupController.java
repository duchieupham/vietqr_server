package com.vietqr.org.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vietqr.org.dto.ResponseMessageDTO;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ClickupController {
    private static final Logger logger = Logger.getLogger(ClickupController.class);

    @PostMapping("clickup/new-assign")
    public ResponseEntity<ResponseMessageDTO> pushNewAssign(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "member") String member) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            final String key = "AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI";
            final String token = "q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk";
            Map<String, Object> data = new HashMap<>();
            data.put("text", "Task mới: " + name + "\n" + "Assign cho: " + member + ".\n"
                    + "Vui lòng truy cập Clickup để ESTIMATE thời gian thực hiện task.");
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
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("pushNewAssign: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
