package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.BankReceiveConnectionEntity;
import com.vietqr.org.entity.MerchantConnectionEntity;
import com.vietqr.org.repository.BankReceiveConnectionRepository;
import com.vietqr.org.service.MerchantConnectionService;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/merchant-connection")
public class MerchantConnectionController {
    private static final Logger logger = Logger.getLogger(MerchantConnectionController.class);

    @Autowired
    private MerchantConnectionService merchantConnectionService;
    @Autowired
    private BankReceiveConnectionRepository bankReceiveConnectionRepository;

    @PostMapping
    public ResponseEntity<Object> createMerchantConnection(@Valid @RequestBody MerchantConnectionRequestDTO requestDTO,
                                                           @RequestParam String mid) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            MerchantConnectionEntity merchantConnection = new MerchantConnectionEntity();
            merchantConnection.setId(UUID.randomUUID().toString());
            merchantConnection.setUrlGetToken(requestDTO.getUrlGetToken());
            merchantConnection.setUrlCallback(requestDTO.getUrlCallback());
            merchantConnection.setUsername(requestDTO.getUsername());
            merchantConnection.setPassword(requestDTO.getPassword());
            merchantConnection.setMid(mid);
            MerchantConnectionEntity createdMerchantConnection = merchantConnectionService.createMerchantConnection(merchantConnection);

            // BankReceiveConnection entity
            BankReceiveConnectionEntity bankReceiveConnection = new BankReceiveConnectionEntity();
            bankReceiveConnection.setId(UUID.randomUUID().toString());
            bankReceiveConnection.setBankId(requestDTO.getBankId());
            bankReceiveConnection.setMidConnectId(createdMerchantConnection.getId());
            bankReceiveConnection.setMid(mid);

            bankReceiveConnectionRepository.save(bankReceiveConnection);

            // response DTO
            MerchantConnectionResponseDTO responseDTO = new MerchantConnectionResponseDTO();
            responseDTO.setId(createdMerchantConnection.getId());
            responseDTO.setMid(createdMerchantConnection.getMid());
            responseDTO.setUrlGetToken(createdMerchantConnection.getUrlGetToken());
            responseDTO.setUrlCallback(createdMerchantConnection.getUrlCallback());

            httpStatus = HttpStatus.CREATED;
            result = responseDTO;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("admin/check-token")
    public ResponseEntity<ResponseMessageDTO> checkTokenCustomerSync(@RequestBody CustomerSyncTokenTestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = getMerchantConnectionToken(dto.getUrl(), dto.getUsername(), dto.getPassword());
            if (result.getStatus().trim().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            //logger.error("checkTokenMerchantConnection: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseMessageDTO getMerchantConnectionToken(String url, String username, String password) {
        ResponseMessageDTO result = null;
        try {
            String key = username.trim() + ":" + password.trim();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + username.trim() + " - password: "
                    + password.trim());
            Map<String, Object> data = new HashMap<>();
            UriComponents uriComponents = null;
            WebClient webClient = null;
            uriComponents = UriComponentsBuilder
                    .fromHttpUrl(
                            url.trim())
                    .buildAndExpand();
            webClient = WebClient.builder()
                    .baseUrl(url.trim())
                    .build();
            Mono<ClientResponse> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange();

            ClientResponse response = responseMono.block();

            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToMerchantConnection: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("access_token") != null) {
                    String accessToken = rootNode.get("access_token").asText();
                    result = new ResponseMessageDTO("SUCCESS", accessToken);
                } else {
                    result = new ResponseMessageDTO("FAILED", "E82");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Token could not be retrieved from: " + url + " - error: " + json);
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }
        } catch (Exception e) {
            //logger.error("Error at getMerchantConnectionToken: " + url + " - " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
        }
        return result;
    }

    // get username-password from merchant name
    @PostMapping("/account/generate")
    public ResponseEntity<AccountCustomerGenerateDTO> generateAccountCustomer(
            @RequestBody AccountCustomerInputDTO dto) {
        AccountCustomerGenerateDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty()) {
                // get count account customer
                Integer customerCounting = merchantConnectionService.getCountingMerchantConnection();
                // generate username - password
                String prefix = "customer";
                String merchantName = dto.getMerchantName().trim().toLowerCase();
                String suffix = "user";
                // Lấy thời gian hiện tại
                LocalDate currentDate = LocalDate.now();
                // Lấy giá trị hai chữ số cuối của năm
                int lastTwoDigitsOfYear = currentDate.getYear() % 100;
                String username = prefix + "-" + merchantName + "-" + suffix + lastTwoDigitsOfYear
                        + (customerCounting + 1);
                //
                String password = encodeBase64(username.trim());
                result = new AccountCustomerGenerateDTO(username, password);
                httpStatus = HttpStatus.OK;
            } else {
                //logger.error("generateAccountCustomer: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            //logger.error("generateAccountCustomer: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    private String encodeBase64(String text) {
        byte[] bytes = text.trim().getBytes();
        String encodedText = Base64.getEncoder().encodeToString(bytes);
        return encodedText;
    }

    @GetMapping()
    public ResponseEntity<Object> getListMerchantConnection(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        Object result;
        HttpStatus httpStatus;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int offset = (page - 1) * size;
            int totalElement;
            List<MerchantConnectionEntity> data = merchantConnectionService.getAllMerchantConnectionEntity(offset, size);
            totalElement = merchantConnectionService.countAllMerchantConnection();

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));

            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            result= pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MerchantConnection: ERROR: getListMerchantConnection " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteMerchantConnection(
            @PathVariable String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (id != null && !id.isEmpty()) {
                merchantConnectionService.deleteMerchantConnectionById(id);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("deleteMerchantConnection: ID IS EMPTY OR INVALID");
                result = new ResponseMessageDTO("FAILED", "E184");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("deleteMerchantConnection: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessageDTO> updateMerchantConnection(
            @Valid @RequestBody MerchantConnectionUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getId() != null && !dto.getId().isEmpty()) {
                MerchantConnectionEntity entity = merchantConnectionService.getMerchanConnectionById(dto.getId());
                if (dto.getMid() != null) {
                    entity.setMid(dto.getMid());
                }
                if (dto.getUrlGetToken() != null) {
                    entity.setUrlGetToken(dto.getUrlGetToken());
                }
                if (dto.getUrlCallback() != null) {
                    entity.setUrlCallback(dto.getUrlCallback());
                }
                if (dto.getPassword() != null) {
                    entity.setPassword(dto.getPassword());
                }
                if (dto.getUsername() != null) {
                    entity.setUsername(dto.getUsername());
                }
                if (dto.getToken() != null) {
                    entity.setToken(dto.getToken());
                    if (dto.getToken().isEmpty()) {
                        entity.setType(0);
                    } else {
                        entity.setType(1);
                    }
                }
                merchantConnectionService.updateMerchantConnectionById(entity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateMerchantConnection: INPUT IS INVALID");
                result = new ResponseMessageDTO("FAILED", "E183");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateMerchantConnection: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
