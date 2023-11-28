package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
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
import com.vietqr.org.dto.AccountCustomerBankInfoDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TerminalActiveVhitekDTO;
import com.vietqr.org.dto.TerminalSyncMBDTO;
import com.vietqr.org.dto.TerminalSyncMBDTOs;
import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.UserVhitekCreateDTO;
import com.vietqr.org.entity.AccountCustomerBankEntity;
import com.vietqr.org.entity.PartnerConnectEntity;
import com.vietqr.org.entity.ServicePartnerCheckerEntity;
import com.vietqr.org.entity.TerminalAddressEntity;
import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.PartnerCheckerService;
import com.vietqr.org.service.PartnerConnectService;
import com.vietqr.org.service.TerminalAddressService;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.util.BankEncryptUtil;
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

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    TerminalAddressService terminalAddressService;

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

    // active terminal
    @PostMapping("service/vhitek/active-terminal")
    public ResponseEntity<ResponseMessageDTO> activeTerminalVhitek2(
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
                    result = insertBankIntoMerchant(dto.getBankId(), partnerConnectEntity.getCustomerSyncId(),
                            dto.getBankAccount(), dto.getUserBankName());
                    if (result.getStatus().equals("SUCCESS")) {
                        TokenDTO tokenDTO = getCustomerSyncToken(partnerConnectEntity);
                        if (tokenDTO != null) {
                            // call API active terminal vhitek
                            Map<String, Object> data = new HashMap<>();
                            data.put("mid", dto.getMid());
                            data.put("iden", dto.getAddress());
                            data.put("user", dto.getUserIdVhitek());
                            data.put("stk", dto.getBankAccount());
                            data.put("name", dto.getUserBankName());
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
                        System.out.println("activeTerminalVhitek: ERROR: " + result.getMessage());
                        logger.error("activeTerminalVhitek: ERROR: " + result.getMessage());
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

    // active terminal admin side
    @PostMapping("admin/service/terminal/active")
    public ResponseEntity<ResponseMessageDTO> activeTerminalVhitekAdmin(
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
                        data.put("stk", dto.getBankAccount());
                        data.put("name", dto.getUserBankName());
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

    // active terminal
    @PostMapping("service/terminal/active")
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
                        data.put("stk", dto.getBankAccount());
                        data.put("name", dto.getUserBankName());
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

    private ResponseMessageDTO insertBankIntoMerchant(String bankId, String customerSyncId, String bankAccount,
            String bankAccountName) {
        ResponseMessageDTO result = null;
        try {
            // 1. check existed bank into vhitek merchant (except bank NGUYEN THU PHUONG MB)
            if (bankId.equals(EnvironmentUtil.getBankIdTestIOT())) {
                System.out.println("insertBankIntoMerchant: BANK ID TEST IOT. Ignore insert.");
                logger.info("insertBankIntoMerchant: BANK ID TEST IOT. Ignore insert.");
                result = new ResponseMessageDTO("SUCCESS", "");
            } else {
                String chekcExistedBank = accountCustomerBankService.checkExistedByBankIdAndCustomerSyncId(bankId,
                        customerSyncId);
                if (chekcExistedBank != null && !chekcExistedBank.trim().isEmpty()) {
                    // 2.1. if existed, ignore
                    System.out.println("insertBankIntoMerchant: BANK EXISTED INTO VHITEK MERCHANT");
                    logger.info("insertBankIntoMerchant: BANK EXISTED INTO VHITEK MERCHANT");
                    result = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    // 2.2.1. if not existed, check is authenticated or not
                    Boolean checkAuthenticated = accountBankReceiveService.getAuthenticatedByBankId(bankId);
                    if (checkAuthenticated != null && checkAuthenticated == true) {
                        // 2.2.2.2. if is authenticated, process add bank flow 2
                        // - get size bank and address merchant
                        AccountCustomerBankInfoDTO accountCustomerBankInfoDTO = accountCustomerBankService
                                .getBankSizeAndAddressByCustomerSyncId(customerSyncId);
                        if (accountCustomerBankInfoDTO != null) {
                            // - get bank token
                            TokenProductBankDTO tokenBankDTO = getBankToken();
                            // - call sync TID
                            String addressTerminal = "";
                            String merchantName = "";
                            Integer nextBank = accountCustomerBankInfoDTO.getTotalBank() + 1;
                            if (accountCustomerBankInfoDTO.getTotalBank() == null
                                    || accountCustomerBankInfoDTO.getTotalBank() == 0) {
                                addressTerminal = accountCustomerBankInfoDTO.getAddress();
                                merchantName = accountCustomerBankInfoDTO.getMerchantName();
                            } else {
                                addressTerminal = accountCustomerBankInfoDTO.getAddress() + " - "
                                        + nextBank;
                                merchantName = accountCustomerBankInfoDTO.getMerchantName() + nextBank;
                            }
                            String bankAccountEncrypted = BankEncryptUtil.encrypt(bankAccount);
                            //
                            TerminalSyncMBDTO terminalSyncDTO = new TerminalSyncMBDTO();
                            terminalSyncDTO.setTerminalId(null);
                            terminalSyncDTO.setTerminalName(merchantName);
                            terminalSyncDTO.setTerminalAddress(addressTerminal);
                            terminalSyncDTO.setProvinceCode("1");
                            terminalSyncDTO.setDistrictCode("6");
                            terminalSyncDTO.setWardsCode("178");
                            terminalSyncDTO.setMccCode("1024");
                            terminalSyncDTO.setFee(0);
                            terminalSyncDTO.setBankCode("311");
                            terminalSyncDTO.setBankCodeBranch("01311038");
                            terminalSyncDTO.setBankAccountNumber(bankAccountEncrypted);
                            terminalSyncDTO.setBankAccountName(bankAccountName);
                            terminalSyncDTO.setBankCurrencyCode("1");
                            boolean syncTID = syncTID(tokenBankDTO, terminalSyncDTO);
                            if (syncTID == true) {
                                // - insert terminal_bank
                                Integer terminalCounting = terminalBankService.getTerminalCounting();
                                Integer nextTerminal = terminalCounting + 1;
                                TerminalBankEntity terminalBankEntity = new TerminalBankEntity();
                                UUID terminalBankId = UUID.randomUUID();
                                terminalBankEntity.setId(terminalBankId.toString());
                                terminalBankEntity.setBankAccountName(bankAccountName);
                                terminalBankEntity.setBankAccountNumber(bankAccountEncrypted);
                                terminalBankEntity.setBankAccountRawNumber(bankAccount);
                                terminalBankEntity.setBankCode("311");
                                terminalBankEntity.setBankCurrencyCode("1");
                                terminalBankEntity.setBankCurrencyName("VND");
                                terminalBankEntity.setBankName("311 - TMCP Quan Doi");
                                terminalBankEntity.setBranchName("NH TMCP QUAN DOI CN SGD 3");
                                terminalBankEntity.setDistrictCode("6");
                                terminalBankEntity.setFee(0);
                                terminalBankEntity.setMccCode("1024");
                                terminalBankEntity.setMccName("Dịch vụ tài chính");
                                terminalBankEntity.setMerchantId("b8324764-3f83-4da0-a75f-aa0f13d0f700");
                                terminalBankEntity.setProvinceCode("Hà Nội update");
                                terminalBankEntity.setStatus(1);
                                terminalBankEntity.setTerminalAddress(addressTerminal);
                                terminalBankEntity.setTerminalId("BLC" + nextTerminal);
                                terminalBankEntity.setTerminalName(merchantName);
                                terminalBankEntity.setWardsCode("178");
                                terminalBankEntity.setWardsName("Phường Cát Linh");
                                terminalBankService.insertTerminalBank(terminalBankEntity);
                                // - insert terminal adress
                                TerminalAddressEntity terminalAddressEntity = new TerminalAddressEntity();
                                UUID terminalAddressId = UUID.randomUUID();
                                terminalAddressEntity.setId(terminalAddressId.toString());
                                terminalAddressEntity.setTerminalBankId(terminalBankId.toString());
                                terminalAddressEntity.setBankAccount(bankAccount);
                                terminalAddressEntity.setBankId(bankId);
                                terminalAddressEntity.setCustomerSyncId(customerSyncId);
                                terminalAddressService.insert(terminalAddressEntity);
                                //
                                //
                                // 2.2.3. add bank into customer_acc_bank
                                AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                                UUID accountCustomerBankId = UUID.randomUUID();
                                accountCustomerBankEntity.setId(accountCustomerBankId.toString());
                                accountCustomerBankEntity.setAccountCustomerId("2fd81e94-66b6-439f-9a2f-b5e2e0ed730a");
                                accountCustomerBankEntity.setBankAccount(bankAccount);
                                accountCustomerBankEntity.setBankId(bankId);
                                accountCustomerBankEntity.setCustomerSyncId(customerSyncId);
                                accountCustomerBankService.insert(accountCustomerBankEntity);
                                // 2.2.4. update is_sync & mms_sync = true
                                accountBankReceiveService.updateMMSActive(true, true, bankId);
                                // 2.2.5. response success
                                System.out.println("insertBankIntoMerchant: SUCCESS SYNC MB & MERCHANT");
                                logger.info("insertBankIntoMerchant: SUCCESS SYNC MB & MERCHANT");
                                result = new ResponseMessageDTO("SUCCESS", "");
                            } else {
                                // - if err, res err. if success get list TID
                                System.out.println("insertBankIntoMerchant: SYNC TID MB FAILED");
                                logger.info("insertBankIntoMerchant: SYNC TID MB FAILED");
                                result = new ResponseMessageDTO("FAILED", "E103");
                            }

                        } else {
                            System.out.println("insertBankIntoMerchant: NOT FOUND MERCHANT INFO");
                            logger.info("insertBankIntoMerchant: NOT FOUND MERCHANT INFO");
                            result = new ResponseMessageDTO("FAILED", "E102");
                        }
                    } else {
                        //
                        // 2.2.2.1. if not authenticated, response err
                        System.out.println("insertBankIntoMerchant: BANK ACCOUNT IS NOT AUTHENTICATED");
                        logger.info("insertBankIntoMerchant: BANK ACCOUNT IS NOT AUTHENTICATED");
                        result = new ResponseMessageDTO("FAILED", "E101");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("insertBankIntoMerchant: ERROR: " + e.toString());
            logger.error("insertBankIntoMerchant: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    // get token bank product
    private TokenProductBankDTO getBankToken() {
        TokenProductBankDTO result = null;
        try {
            String key = EnvironmentUtil.getUserBankAccess() + ":" + EnvironmentUtil.getPasswordBankAccess();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl() + "oauth2/v1/token")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getBankUrl()
                            + "oauth2/v1/token")
                    .build();
            // Call POST API
            TokenProductBankDTO response = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                    .exchange()
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenProductBankDTO.class);
                        } else {
                            clientResponse.body((clientHttpResponse, context) -> {
                                logger.info(clientHttpResponse.getBody().collectList().block().toString());
                                return clientHttpResponse.getBody();
                            });
                            return null;
                        }
                    })
                    .block();
            result = response;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    private boolean syncTID(TokenProductBankDTO tokenDTO, TerminalSyncMBDTO dto) {
        boolean result = false;
        try {
            // call API active terminal vhitek
            TerminalSyncMBDTOs list = new TerminalSyncMBDTOs();
            list.setTerminals(Collections.singletonList(dto));
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(list);
            //
            //
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getBankUrl() + "")
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                    .header("clientMessageId", UUID.randomUUID().toString())
                    .header("secretKey", "vuSMiHQ3tH2auAVHzXQiMgQQCzcdlpvq3Bb0wQRF4dBxdjojMj0LQnGUPE24bGqr")
                    .header("username", "MB_BLC")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                result = true;
                String json = response.bodyToMono(String.class).block();
                System.out.println("syncTID: Response: " + json);
                logger.info("syncTID: Response: " + json);
            } else {
                result = false;
                String json = response.bodyToMono(String.class).block();
                System.out.println("syncTID: Response: " + json);
                logger.info("syncTID: Response: " + json);
            }
        } catch (Exception e) {
            System.out.println("syncTID: ERROR: " + e.toString());
            logger.error("syncTID: ERROR: " + e.toString());
        }
        return result;
    }
}
