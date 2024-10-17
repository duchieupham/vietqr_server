package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.vietqr.org.entity.*;
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
import com.vietqr.org.dto.TerminalSyncMBDTO;
import com.vietqr.org.dto.TerminalSyncMBDTOs;
import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.UserVhitekCreateDTO;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.PartnerCheckerService;
import com.vietqr.org.service.PartnerConnectService;
import com.vietqr.org.service.TerminalAddressService;
import com.vietqr.org.service.TerminalBankService;
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
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
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
            } else {
                logger.info("VhitekActiveController: getCustomerSyncToken: Token could not be retrieved");
            }
        } catch (Exception e) {
            logger.info("VhitekActiveController: getCustomerSyncToken: ERROR: " + e.toString());
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
                            //System.out.println("checkValidUser: Response: " + json);
                            logger.error("checkValidUser: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            //
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    String userId = rootNode.get("detail").get("details").asText();
                                    if (userId != null && !userId.trim().isEmpty()) {
                                        logger.info("checkValidUser: EMAIL IS EXISTED IN VHITEK SYSTEM: " + userId);
                                        result = new ResponseMessageDTO("SUCCESS", userId);
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        //System.out.println("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                        logger.error("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                        result = new ResponseMessageDTO("CHECK", "C08");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    //System.out.println("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                    logger.error("checkValidUser: EMAIL IS NOT EXISTED IN VHITEK SYSTEM");
                                    result = new ResponseMessageDTO("CHECK", "C08");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                //System.out.println("checkValidUser: Response ERROR: " + json);
                                logger.error("checkValidUser: Response ERROR: " + json);
                                result = new ResponseMessageDTO("FAILED", "E100");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            //System.out.println("checkValidUser: Response ERROR: " + json);
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
            //System.out.println("checkValidUser: ERROR: " + e.toString());
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
                            //System.out.println("createUserVhitek: Response: " + json);
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
                                        //System.out.println("createUserVhitek: SUCCESS: " + userId);
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
                                        //System.out.println("createUserVhitek: Response: " + json);
                                        logger.error("createUserVhitek: Response: " + json);
                                        result = new ResponseMessageDTO("FAILED", "E05");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    //System.out.println("createUserVhitek: Response: " + json);
                                    logger.error("createUserVhitek: Response: " + json);
                                    result = new ResponseMessageDTO("FAILED", "E05");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                //System.out.println("createUserVhitek: Response: " + json);
                                logger.error("createUserVhitek: Response: " + json);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            //System.out.println("createUserVhitek: Response: " + json);
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
            //System.out.println("createUserVhitek: ERROR: " + e.toString());
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
        logger.info("VhitekActiveController: activeTerminalVhitek2: Request: " + dto);
        try {
            if (dto != null) {
                // get service urls
                PartnerConnectEntity partnerConnectEntity = partnerConnectService
                        .getPartnerConnectByServiceName(serviceVhitekActive);
                if (partnerConnectEntity != null) {
                    result = insertBankIntoMerchant(dto.getBankId(), partnerConnectEntity.getCustomerSyncId(),
                            dto.getBankAccount(), dto.getUserBankName());
                    logger.info("VhitekActiveController: activeTerminalVhitek2: insertBankIntoMerchant: SUCCESS: " + result);
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
                            LocalDateTime current = LocalDateTime.now();
                            long timeRequest = current.toEpochSecond(ZoneOffset.UTC);
                            logger.info("VhitekActiveController: activeTerminalVhitek2: Response: " + data
                                    + " at: " + timeRequest);
                            Mono<ClientResponse> responseMono = webClient.post()
                                    .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(data))
                                    .exchange();
                            ClientResponse response = responseMono.block();
                            if (response.statusCode().is2xxSuccessful()) {
                                String json = response.bodyToMono(String.class).block();
                                current = LocalDateTime.now();
                                long timeResponse = current.toEpochSecond(ZoneOffset.UTC);
                                logger.info("VhitekActiveController: activeTerminalVhitek2: Response: " + json
                                + " at: " + timeResponse);
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode rootNode = objectMapper.readTree(json);
                                if (rootNode.get("detail") != null) {
                                    Boolean status = rootNode.get("detail").get("status").asBoolean();
                                    if (status != null && status == true) {
                                        logger.info("createUserVhitek: SUCCESS: ");
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
                                        //System.out.println("activeTerminalVhitek: Response: " + json);
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
                                    //System.out.println("activeTerminalVhitek: Response: " + json);
                                    logger.error("activeTerminalVhitek: Response: " + json);
                                    result = new ResponseMessageDTO("FAILED", "E05");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                current = LocalDateTime.now();
                                long timeResponse = current.toEpochSecond(ZoneOffset.UTC);
                                String json = response.bodyToMono(String.class).block();
                                logger.error("VhitekActiveController: activeTerminalVhitek2: Response: " + json
                                        + " at: " + timeResponse);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E99");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        //System.out.println("activeTerminalVhitek: ERROR: " + result.getMessage());
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
            //System.out.println("activeTerminalVhitek: ERROR: " + e.toString());
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
                            //System.out.println("activeTerminalVhitek: Response: " + json);
                            logger.info("activeTerminalVhitek: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    logger.info("createUserVhitek: SUCCESS: ");
                                    //System.out.println("createUserVhitek: SUCCESS: ");
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
                                    //System.out.println("activeTerminalVhitek: Response: " + json);
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
                                //System.out.println("activeTerminalVhitek: Response: " + json);
                                logger.error("activeTerminalVhitek: Response: " + json);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            //System.out.println("activeTerminalVhitek: Response: " + json);
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
            //System.out.println("activeTerminalVhitek: ERROR: " + e.toString());
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
                            //System.out.println("activeTerminalVhitek: Response: " + json);
                            logger.info("activeTerminalVhitek: Response: " + json);
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(json);
                            if (rootNode.get("detail") != null) {
                                Boolean status = rootNode.get("detail").get("status").asBoolean();
                                if (status != null && status == true) {
                                    logger.info("createUserVhitek: SUCCESS: ");
                                    //System.out.println("createUserVhitek: SUCCESS: ");
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
                                    //System.out.println("activeTerminalVhitek: Response: " + json);
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
                                //System.out.println("activeTerminalVhitek: Response: " + json);
                                logger.error("activeTerminalVhitek: Response: " + json);
                                result = new ResponseMessageDTO("FAILED", "E05");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            String json = response.bodyToMono(String.class).block();
                            //System.out.println("activeTerminalVhitek: Response: " + json);
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
            //System.out.println("activeTerminalVhitek: ERROR: " + e.toString());
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
            // 2.2.1. if not existed, check is authenticated or not
            Boolean checkAuthenticated = accountBankReceiveService.getAuthenticatedByBankId(bankId);
            if (checkAuthenticated != null && checkAuthenticated) {
                AccountBankReceiveEntity accountBankReceiveEntity
                        = accountBankReceiveService.getAccountBankById(bankId);
                AccountCustomerBankEntity accountCustomerBankEntity = accountCustomerBankService
                        .getAccountCustomerBankByBankIdAndMerchantId(bankId, EnvironmentUtil.getDefaultCustomerSyncIdIot());
                if (accountCustomerBankEntity == null) {
                    accountCustomerBankEntity = new AccountCustomerBankEntity();
                    accountCustomerBankEntity.setBankId(bankId);
                    accountCustomerBankEntity.setCustomerSyncId(EnvironmentUtil.getDefaultCustomerSyncIdIot());
                    accountCustomerBankEntity.setBankAccount(bankAccount);
                    accountCustomerBankEntity.setAccountCustomerId("");
                    accountCustomerBankEntity.setId(UUID.randomUUID().toString());
                    accountCustomerBankService.insert(accountCustomerBankEntity);
                }
                if (accountBankReceiveEntity.isMmsActive()) {
                    TerminalAddressEntity terminalAddressEntity = terminalAddressService
                            .getTerminalAddressByBankIdAndCustomerSyncId(bankId, EnvironmentUtil.getDefaultCustomerSyncIdIot());
                    if (terminalAddressEntity == null) {
                        terminalAddressEntity = new TerminalAddressEntity();
                        TerminalBankEntity terminalBankEntity = terminalBankService.getTerminalBankByBankAccount(bankAccount);
                        terminalAddressEntity.setBankId(bankId);
                        terminalAddressEntity.setTerminalBankId(terminalBankEntity.getId());
                        terminalAddressEntity.setCustomerSyncId(EnvironmentUtil.getDefaultCustomerSyncIdIot());
                        terminalAddressEntity.setBankAccount(accountBankReceiveEntity.getBankAccount());
                        terminalAddressEntity.setId(UUID.randomUUID().toString());
                        terminalAddressService.insert(terminalAddressEntity);
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
            } else {
                //
                // 2.2.2.1. if not authenticated, response err
                //System.out.println("insertBankIntoMerchant: BANK ACCOUNT IS NOT AUTHENTICATED");
                logger.info("insertBankIntoMerchant: BANK ACCOUNT IS NOT AUTHENTICATED");
                result = new ResponseMessageDTO("FAILED", "E101");
            }

        } catch (Exception e) {
            //System.out.println("insertBankIntoMerchant: ERROR: " + e.toString());
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
                //System.out.println("syncTID: Response: " + json);
                logger.info("syncTID: Response: " + json);
            } else {
                result = false;
                String json = response.bodyToMono(String.class).block();
                //System.out.println("syncTID: Response: " + json);
                logger.info("syncTID: Response: " + json);
            }
        } catch (Exception e) {
            //System.out.println("syncTID: ERROR: " + e.toString());
            logger.error("syncTID: ERROR: " + e.toString());
        }
        return result;
    }
}
