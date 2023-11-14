package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TerminalActiveVhitekDTO;
import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.dto.UserVhitekCreateDTO;
import com.vietqr.org.entity.PartnerConnectEntity;
import com.vietqr.org.entity.ServicePartnerCheckerEntity;
import com.vietqr.org.service.PartnerCheckerService;
import com.vietqr.org.service.PartnerConnectService;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VhitekActiveController {
    private static final Logger logger = Logger.getLogger(VhitekActiveController.class);

    @Autowired
    PartnerConnectService partnerConnectService;

    @Autowired
    PartnerCheckerService partnerCheckerService;

    private TokenDTO getCustomerSyncToken(PartnerConnectEntity entity) {
        TokenDTO result = null;
        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(entity.getUrl1())
                    .buildAndExpand();
            WebClient webClient = WebClient.builder()
                    .baseUrl(entity.getUrl1())
                    .build();
            Map<String, Object> data = new HashMap<>();
            String key = entity.getUsernameBasic() + ":" + entity.getPasswordBasic();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("VhitekActiveController: getCustomerSyncToken: encodedKey: " + encodedKey);
            System.out.println("VhitekActiveController: getCustomerSyncToken: encodedKey: " + encodedKey);
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println(
                                "VhitekActiveController: get token: status code: " + clientResponse.statusCode());
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenDTO.class);
                        } else {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.info("Error response: " + error);
                                        return Mono.empty();
                                    });
                        }
                    });
            Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
                logger.info("VhitekActiveController: getCustomerSyncToken: token got: " + result.getAccess_token());
                System.out.println(
                        "VhitekActiveController: getCustomerSyncToken: token got: " + result.getAccess_token());
            } else {
                logger.info("VhitekActiveController: getCustomerSyncToken: Token could not be retrieved");
                System.out.println("VhitekActiveController: getCustomerSyncToken: Token could not be retrieved");
            }
        } catch (Exception e) {
            logger.info("VhitekActiveController: getCustomerSyncToken: ERROR: " + e.toString());
            System.out.println("VhitekActiveController: getCustomerSyncToken:  ERROR: " + e.toString());
        }
        return result;
    }

    @GetMapping("service/vhitek/check-user")
    public ResponseEntity<ResponseMessageDTO> checkValidUser(
            @RequestParam(value = "email") String email) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        String serviceVhitekActive = EnvironmentUtil.getServiceVhitekActive();
        try {
            if (email != null && !email.trim().isEmpty()) {
                // get service urls
                PartnerConnectEntity partnerConnectEntity = partnerConnectService
                        .getPartnerConnectByServiceName(serviceVhitekActive);
                if (partnerConnectEntity != null) {
                    // get token
                    TokenDTO tokenDTO = getCustomerSyncToken(partnerConnectEntity);
                    if (tokenDTO != null) {
                        // call api check email
                        WebClient webClient = WebClient.builder()
                                .baseUrl(partnerConnectEntity.getUrl2() + email)
                                .build();
                        Mono<ClientResponse> responseMono = webClient.get()
                                .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                                .exchange();
                        ClientResponse response = responseMono.block();
                        if (response.statusCode().is2xxSuccessful()) {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("checkValidUser: Response: " + json);
                            logger.error("checkValidUser: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            //
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    String userId = rootNode.get("detail").get("details").asText();
                                    if (userId != null && !userId.trim().isEmpty()) {
                                        System.out.println(
                                                "checkValidUser: EMAIL IS EXISTED IN VHITEK SYSTEM: " + userId);
                                        logger.info("checkValidUser: EMAIL IS EXISTED IN VHITEK SYSTEM: " + userId);
                                        result = new ResponseMessageDTO("SUCCESS", userId);
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        System.out.println("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                        logger.error("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                        result = new ResponseMessageDTO("CHECK", "C08");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    System.out.println("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                    logger.error("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                    result = new ResponseMessageDTO("CHECK", "C08");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                System.out.println("checkValidUser: Response ERROR: " + json);
                                logger.error("checkValidUser: Response ERROR: " + json);
                                result = new ResponseMessageDTO("FAILED", "E100");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("checkValidUser: Response ERROR: " + json);
                            logger.error("checkValidUser: Response ERROR: " + json);
                            result = new ResponseMessageDTO("FAILED", "E100");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E99");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E98");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("checkValidUser: ERROR: " + e.toString());
            logger.error("checkValidUser: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //
    @PostMapping("service/vhitek/create-user")
    public ResponseEntity<ResponseMessageDTO> createUserVhitek(
            @RequestBody UserVhitekCreateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        String serviceVhitekActive = EnvironmentUtil.getServiceVhitekActive();
        try {
            if (dto != null) {
                // get service urls
                PartnerConnectEntity partnerConnectEntity = partnerConnectService
                        .getPartnerConnectByServiceName(serviceVhitekActive);
                if (partnerConnectEntity != null) {
                    TokenDTO tokenDTO = getCustomerSyncToken(partnerConnectEntity);
                    if (tokenDTO != null) {
                        // call API create user vhitek
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", dto.getName());
                        data.put("phone", dto.getPhoneNo());
                        data.put("email", dto.getEmail());
                        data.put("pwd", dto.getPassword());
                        //
                        WebClient webClient = WebClient.builder()
                                .baseUrl(partnerConnectEntity.getUrl3())
                                .build();
                        Mono<ClientResponse> responseMono = webClient.post()
                                .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(data))
                                .exchange();
                        ClientResponse response = responseMono.block();
                        if (response.statusCode().is2xxSuccessful()) {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("createUserVhitek: Response: " + json);
                            logger.info("createUserVhitek: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            // result = new ResponseMessageDTO("SUCCESS", "");
                            // httpStatus = HttpStatus.OK;
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    String userId = rootNode.get("detail").get("idUser").asText();
                                    if (userId != null && !userId.trim().isEmpty()) {
                                        logger.info("createUserVhitek: SUCCESS: " + userId);
                                        System.out.println("createUserVhitek: SUCCESS: " + userId);
                                        ServicePartnerCheckerEntity partnerCheckerEntity = new ServicePartnerCheckerEntity();
                                        UUID uuid = UUID.randomUUID();
                                        partnerCheckerEntity.setId(uuid.toString());
                                        partnerCheckerEntity.setService(serviceVhitekActive + "_REGISTER");
                                        partnerCheckerEntity.setData1(dto.getUserId());
                                        partnerCheckerEntity.setData2(dto.getEmail());
                                        partnerCheckerEntity.setData3(dto.getPhoneNo());
                                        partnerCheckerEntity.setData4(dto.getName());
                                        partnerCheckerEntity.setData5(userId);
                                        LocalDateTime currentDateTime = LocalDateTime.now();
                                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                        partnerCheckerEntity.setTime(time);
                                        partnerCheckerService.insert(partnerCheckerEntity);
                                        result = new ResponseMessageDTO("SUCCESS", userId);
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        System.out.println("createUserVhitek: Response: " + json);
                                        logger.error("createUserVhitek: Response: " + json);
                                        result = new ResponseMessageDTO("FAILED", "E05");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    System.out.println("createUserVhitek: Response: " + json);
                                    logger.error("createUserVhitek: Response: " + json);
                                    result = new ResponseMessageDTO("FAILED", "E05");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                System.out.println("createUserVhitek: Response: " + json);
                                logger.error("createUserVhitek: Response: " + json);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("createUserVhitek: Response: " + json);
                            logger.error("createUserVhitek: Response: " + json);
                            result = new ResponseMessageDTO("FAILED", "E05");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E99");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E98");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("createUserVhitek: ERROR: " + e.toString());
            logger.error("createUserVhitek: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // active
    @PostMapping("service/vhitek/active-terminal")
    public ResponseEntity<ResponseMessageDTO> activeTerminalVhitek(
            @RequestBody TerminalActiveVhitekDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        String serviceVhitekActive = EnvironmentUtil.getServiceVhitekActive();
        try {
            if (dto != null) {
                // get service urls
                PartnerConnectEntity partnerConnectEntity = partnerConnectService
                        .getPartnerConnectByServiceName(serviceVhitekActive);
                if (partnerConnectEntity != null) {
                    TokenDTO tokenDTO = getCustomerSyncToken(partnerConnectEntity);
                    if (tokenDTO != null) {
                        // call API active terminal vhitek
                        Map<String, Object> data = new HashMap<>();
                        data.put("mid", dto.getMid());
                        data.put("iden", dto.getAddress());
                        data.put("user", dto.getUserIdVhitek());
                        //
                        WebClient webClient = WebClient.builder()
                                .baseUrl(partnerConnectEntity.getUrl4())
                                .build();
                        Mono<ClientResponse> responseMono = webClient.post()
                                .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(data))
                                .exchange();
                        ClientResponse response = responseMono.block();
                        if (response.statusCode().is2xxSuccessful()) {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("activeTerminalVhitek: Response: " + json);
                            logger.info("activeTerminalVhitek: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    logger.info("createUserVhitek: SUCCESS: ");
                                    System.out.println("createUserVhitek: SUCCESS: ");
                                    ServicePartnerCheckerEntity partnerCheckerEntity = new ServicePartnerCheckerEntity();
                                    UUID uuid = UUID.randomUUID();
                                    partnerCheckerEntity.setId(uuid.toString());
                                    partnerCheckerEntity.setService(serviceVhitekActive);
                                    partnerCheckerEntity.setData1(dto.getUserId());
                                    partnerCheckerEntity.setData2(dto.getMid());
                                    partnerCheckerEntity.setData3(dto.getAddress());
                                    partnerCheckerEntity.setData4(dto.getUserIdVhitek());
                                    LocalDateTime currentDateTime = LocalDateTime.now();
                                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                    partnerCheckerEntity.setTime(time);
                                    partnerCheckerService.insert(partnerCheckerEntity);
                                    result = new ResponseMessageDTO("SUCCESS", "");
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    System.out.println("activeTerminalVhitek: Response: " + json);
                                    logger.error("activeTerminalVhitek: Response: " + json);
                                    Integer errCode = rootNode.get("detail").get("code").asInt();
                                    if (errCode != null && errCode == -2) {
                                        result = new ResponseMessageDTO("CHECK", "C09");
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        result = new ResponseMessageDTO("FAILED", "E05");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }

                                }
                            } else {
                                System.out.println("activeTerminalVhitek: Response: " + json);
                                logger.error("activeTerminalVhitek: Response: " + json);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            System.out.println("activeTerminalVhitek: Response: " + json);
                            logger.error("activeTerminalVhitek: Response: " + json);
                            result = new ResponseMessageDTO("FAILED", "E05");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E99");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E98");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("activeTerminalVhitek: ERROR: " + e.toString());
            logger.error("activeTerminalVhitek: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
