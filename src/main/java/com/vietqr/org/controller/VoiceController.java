package com.vietqr.org.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransactionVoiceRequestDTO;
import com.vietqr.org.entity.AccountSettingEntity;
import com.vietqr.org.service.AccountSettingService;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VoiceController {
    private static final Logger logger = Logger.getLogger(VoiceController.class);

    @Autowired
    AccountSettingService accountSettingService;

    @PostMapping("voice/transaction")
    public ResponseEntity<ResponseMessageDTO> getTransactionVoice(@RequestBody TransactionVoiceRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            //
            if (dto != null) {
                AccountSettingEntity entity = accountSettingService.getAccountSettingEntity(dto.getUserId());
                if (entity != null) {
                    if (dto.getType() == 0) {
                        if (entity.isVoiceMobile() == true) {
                            // call function get voice
                            String voiceUrl = getUrlVoice(dto.getAmount());
                            result = new ResponseMessageDTO("SUCCESS", voiceUrl);
                            httpStatus = HttpStatus.OK;
                        } else {
                            logger.info("getTransactionVoice: USER DISABLE VOICE");
                            result = new ResponseMessageDTO("FAILED", "E69");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else if (dto.getType() == 1) {
                        if (entity.isVoiceMobileKiot() == true) {
                            // call function get voice
                            String voiceUrl = getUrlVoice(dto.getAmount());
                            result = new ResponseMessageDTO("SUCCESS", voiceUrl);
                            httpStatus = HttpStatus.OK;
                        } else {
                            logger.info("getTransactionVoice: USER DISABLE VOICE");
                            result = new ResponseMessageDTO("FAILED", "E69");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else if (dto.getType() == 2) {
                        if (entity.isVoiceWeb() == true) {
                            // call function get voice
                            String voiceUrl = getUrlVoice(dto.getAmount());
                            result = new ResponseMessageDTO("SUCCESS", voiceUrl);
                            httpStatus = HttpStatus.OK;
                        } else {
                            logger.info("getTransactionVoice: USER DISABLE VOICE");
                            result = new ResponseMessageDTO("FAILED", "E69");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error("getTransactionVoice: WRONG TYPE");
                        result = new ResponseMessageDTO("FAILED", "E68");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("getTransactionVoice: NOT FOUND USER SETTING");
                    result = new ResponseMessageDTO("FAILED", "E67");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getTransactionVoice: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            //
        } catch (Exception e) {
            logger.error("Error at getTransactionVoice: " + e.toString());
            System.out.println("Error at getTransactionVoice: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    String getUrlVoice(String text) {
        String result = "";
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("voice_code", EnvironmentUtil.getVoiceCode());
            data.put("input_text", text);
            data.put("appId", EnvironmentUtil.getAppIdVoice());
            data.put("audio_type", EnvironmentUtil.getVoiceType());
            data.put("speed_rate", EnvironmentUtil.getSpeedRate());
            data.put("bitrate", EnvironmentUtil.getBitRate());
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getVoiceRequestUrl())
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getVoiceRequestUrl())
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response getUrlVoice: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                String url = rootNode.get("audio_url").asText();
                result = url;
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response getUrlVoice: " + json);
            }
        } catch (Exception e) {
            logger.error("getUrlVoice: ERROR: " + e.toString());
        }
        return result;
    }
}
