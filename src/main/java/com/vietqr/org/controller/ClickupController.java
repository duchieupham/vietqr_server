package com.vietqr.org.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    // @PostMapping("clickup/new-assign")
    // public ResponseEntity<ResponseMessageDTO> pushNewAssign(
    // @RequestParam(value = "name") String name,
    // @RequestParam(value = "member") String member) {
    // ResponseMessageDTO result = null;
    // HttpStatus httpStatus = null;
    // try {
    // final String key = "AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI";
    // final String token = "q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk";
    // Map<String, Object> data = new HashMap<>();
    // data.put("text", "Task mới: " + name + "\n" + "Assign cho: " + member + ".\n"
    // + "Vui lòng truy cập Clickup để ESTIMATE thời gian thực hiện task.");
    // final String url =
    // "https://chat.googleapis.com/v1/spaces/AAAAEkpkd2A/messages?key=" + key
    // + "&token=" + token;
    // // Build URL with PathVariable
    // UriComponents uriComponents = UriComponentsBuilder
    // .fromHttpUrl(url).buildAndExpand();
    // // Create WebClient with authorization header
    // WebClient webClient = WebClient.builder()
    // .baseUrl(uriComponents.toUriString())
    // .build();
    // //
    // Mono<ClientResponse> responseMono = webClient.post()
    // // .uri("/bank/api/transaction-sync")
    // .contentType(MediaType.APPLICATION_JSON)
    // .body(BodyInserters.fromValue(data))
    // .exchange();
    // ClientResponse response = responseMono.block();
    // if (response.statusCode().is2xxSuccessful()) {
    // result = new ResponseMessageDTO("SUCCESS", "");
    // httpStatus = HttpStatus.OK;
    // } else {
    // result = new ResponseMessageDTO("FAILED", "E05");
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // } catch (Exception e) {
    // logger.error("pushNewAssign: ERROR: " + e.toString());
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // return new ResponseEntity<>(result, httpStatus);
    // }

    @PostMapping("clickup/task-supporter")
    public ResponseEntity<ResponseMessageDTO> pushTaskAction(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "member") String member,
            @RequestParam(value = "due") String due,
            @RequestParam(value = "link") String link) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            final String key = "AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI";
            final String token = "q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk";
            // process data
            String taskName = "Tên Task: " + name;
            String assignee = "Người thực hiện: " + member;
            String dueDate = "Due date: " + due;

            String title = "";
            String content = "";
            String description = "";
            if (type != null && type.trim().equals("1")) {
                title = "TASK MỚI";
                description = "Truy cập đường dẫn " + link + " để estimate thời gian thực hiện.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n\n" + description;
            } else if (type != null && type.trim().equals("2")) {
                title = "TASK CẬP NHẬT DUE DATE";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n" + dueDate + "\n\n" + description;
            } else if (type != null && type.trim().equals("3")) {
                title = "TASK SẴN SÀNG TEST";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n\n" + description;
            } else if (type != null && type.trim().equals("4")) {
                title = "TASK ĐANG TRONG QUÁ TRÌNH TEST";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n\n" + description;
            } else if (type != null && type.trim().equals("5")) {
                title = "TASK HOÀN TẤT TEST";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n\n" + description;
            } else if (type != null && type.trim().equals("6")) {
                title = "TASK BỊ REJECTED";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n\n" + description;
            } else if (type != null && type.trim().equals("7")) {
                title = "TASK TẠM THỜI BỊ HOÃN";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n\n" + description;
            } else if (type != null && type.trim().equals("8")) {
                title = "TASK TRỄ DEADLINE";
                description = "Truy cập đường dẫn " + link + " để cập nhật lại DUE DATE và ghi chú lại lý do.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n" + dueDate + "\n\n" + description;
            } else if (type != null && type.trim().equals("9")) {
                title = "TASK CÓ BÌNH LUẬN MỚI";
                description = "Truy cập đường dẫn " + link + " để xem thông tin chi tiết.";
                content = title + "\n\n" + taskName + "\n" + assignee + "\n" + dueDate + "\n\n" + description;
            }

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
