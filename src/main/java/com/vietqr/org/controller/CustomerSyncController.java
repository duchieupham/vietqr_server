package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.CusSyncApiInfoDTO;
import com.vietqr.org.dto.CusSyncEcInfoDTO;
import com.vietqr.org.dto.CustomerSyncInsertDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.dto.CustomerSyncStatusDTO;
import com.vietqr.org.dto.CustomerSyncTokenTestDTO;
import com.vietqr.org.dto.CustomerSyncUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.AccountCustomerBankEntity;
import com.vietqr.org.entity.AccountCustomerEntity;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.service.AccountBankReceivePersonalService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.AccountCustomerService;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class CustomerSyncController {
    private static final Logger logger = Logger.getLogger(CustomerSyncController.class);

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    @Autowired
    AccountCustomerService accountCustomerService;

    @GetMapping("customer-sync")
    public ResponseEntity<List<CustomerSyncListDTO>> getCustomerSyncList() {
        List<CustomerSyncListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = customerSyncService.getCustomerSyncList();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("customer-sync/information")
    public ResponseEntity<ResponseMessageDTO> updateCustomerSync(@RequestBody CustomerSyncUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getCustomerSyncId() != null) {
                customerSyncService.updateCustomerSync(dto.getUrl(), dto.getIp(), dto.getPassword(), dto.getPort(),
                        dto.getSuffix(), dto.getUsername(), dto.getCustomerSyncId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateCustomerSync: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("customer-sync/information")
    public ResponseEntity<Object> getCustomerSyncInfo(@RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // 0 => API Service
            // 1 => E-Commerce
            Integer cusSyncType = customerSyncService.checkCustomerSyncTypeById(id);
            if (cusSyncType != null && cusSyncType == 0) {
                CusSyncApiInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncApiInfo(id);
                if (cusSyncDTO != null) {
                    result = cusSyncDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    System.out.println("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
                    logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
                    result = new ResponseMessageDTO("FAILED", "E81");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (cusSyncType != null && cusSyncType == 1) {
                CusSyncEcInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncEcInfo(id);
                if (cusSyncDTO != null) {
                    result = cusSyncDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    System.out.println("getCustomerSyncInfo: NOT FOUND CUS_SYNC - cusSyncDTO = null");
                    logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
                    result = new ResponseMessageDTO("FAILED", "E81");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                System.out.println("getCustomerSyncInfo: NOT FOUND TYPE");
                logger.error("getCustomerSyncInfo: NOT FOUND TYPE");
                result = new ResponseMessageDTO("FAILED", "E80");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // test get token customer
    @PostMapping("customer-sync/check-token")
    public ResponseEntity<ResponseMessageDTO> checkTokenCustomerSync(@RequestBody CustomerSyncTokenTestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = getCustomerSyncToken(dto.getUrl(), dto.getUsername(), dto.getPassword());
            if (result.getStatus().trim().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("checkTokenCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get customer token
    private ResponseMessageDTO getCustomerSyncToken(String url, String username, String password) {
        ResponseMessageDTO result = null;
        try {
            String key = username.trim() + ":" + password.trim();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + username.trim() + " - password: "
                    + password.trim());
            // System.out.println("key: " + encodedKey + " - username: " +
            // entity.getUsername() + " - password: "
            // + entity.getPassword());

            UriComponents uriComponents = null;
            WebClient webClient = null;
            uriComponents = UriComponentsBuilder
                    .fromHttpUrl(
                            url.trim() + "/api/token_generate")
                    .buildAndExpand();
            webClient = WebClient.builder()
                    .baseUrl(url.trim() + "/api/token_generate")
                    .build();
            // System.out.println("uriComponents: " + uriComponents.toString());
            Mono<ClientResponse> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .exchange();

            ClientResponse response = responseMono.block();
            //
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json);
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
            logger.error("Error at getCustomerSyncToken: " + url + " - " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
        }
        return result;
    }

    // get systemPassword for customer
    @GetMapping("customer-sync/system-password")
    public ResponseEntity<ResponseMessageDTO> getSystemPasswordForCustomer(
            @RequestParam(value = "username") String username) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (username != null && !username.trim().isEmpty()) {
                String password = encodeBase64(username.trim());
                result = new ResponseMessageDTO("SUCCESS", password);
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getSystemPasswordForCustomer: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String encodeBase64(String text) {
        byte[] bytes = text.trim().getBytes();
        String encodedText = Base64.getEncoder().encodeToString(bytes);
        return encodedText;
    }

    // update status customer sync
    @PostMapping("customer-sync/status")
    public ResponseEntity<ResponseMessageDTO> updateCustomerSyncStatus(@RequestBody CustomerSyncStatusDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                boolean active = false;
                if (dto.getStatus() == 1) {
                    active = true;
                }
                customerSyncService.updateCustomerSyncStatus(active, dto.getCustomerSyncId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateCustomerSyncStatus: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add customer sync
    @PostMapping("customer-sync")
    public ResponseEntity<ResponseMessageDTO> insertNewCustomerSync(@RequestBody CustomerSyncInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // 1. check existed merchant name
            if (dto != null && dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty()) {
                List<String> checkExistedMerchant = customerSyncService.checkExistedMerchant(dto.getMerchantName());
                if (checkExistedMerchant != null && !checkExistedMerchant.isEmpty()) {
                    // if existed: -> show msg
                    logger.error("insertNewCustomerSync: EXISTED MERCHANT NAME");
                    result = new ResponseMessageDTO("FAILED", "E85");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    ///
                } else {
                    // if not: pass
                    //
                    String checkExistedAddress = terminalBankService.checkExistedTerminalAddress(dto.getAddress());
                    // 2. check address
                    if (checkExistedAddress != null && !checkExistedAddress.isEmpty()) {
                        // if existed -> show msg
                        logger.error("insertNewCustomerSync: EXISTED TERMINAL ADDRESS");
                        result = new ResponseMessageDTO("FAILED", "E86");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        // if not -> pass
                        //
                        // 4. check Env
                        if (EnvironmentUtil.isProduction() == false) {
                            //////////
                            // test:
                            // 5. check bankAccount existed
                            UUID uuidAccountBank = UUID.randomUUID();
                            String checkExistedBankAccount = accountBankReceiveService
                                    .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                            if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                                // if existed -> passed
                            } else {
                                // if not -> add bank_Account (is_sync = true and is_authenticated = true)
                                AccountBankReceiveEntity accountBankReceiveEntity = new AccountBankReceiveEntity();
                                accountBankReceiveEntity.setId(uuidAccountBank.toString());
                                accountBankReceiveEntity.setBankAccount(dto.getBankAccount());
                                accountBankReceiveEntity.setBankAccountName(dto.getUserBankName());
                                accountBankReceiveEntity.setBankTypeId(EnvironmentUtil.getBankTypeIdRecharge());
                                accountBankReceiveEntity.setAuthenticated(true);
                                accountBankReceiveEntity.setSync(true);
                                accountBankReceiveEntity.setWpSync(false);
                                accountBankReceiveEntity.setMmsActive(false);
                                accountBankReceiveEntity.setNationalId("");
                                accountBankReceiveEntity.setPhoneAuthenticated("");
                                accountBankReceiveEntity.setStatus(true);
                                accountBankReceiveEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                                accountBankReceiveEntity.setRpaSync(false);
                                accountBankReceiveEntity.setPassword("");
                                accountBankReceiveEntity.setUsername("");
                                accountBankReceiveService.insertAccountBank(accountBankReceiveEntity);
                                //
                                UUID uuid2 = UUID.randomUUID();
                                BankReceivePersonalEntity bankReceivePersonalEntity = new BankReceivePersonalEntity();
                                bankReceivePersonalEntity.setId(uuid2.toString());
                                bankReceivePersonalEntity.setBankId(uuidAccountBank.toString());
                                bankReceivePersonalEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                                accountBankReceivePersonalService
                                        .insertAccountBankReceivePersonal(bankReceivePersonalEntity);
                                //
                            }
                            //
                            // 6. add customer_sync (with status)
                            // check valid IP+PORT OR url and suffix URL
                            String url = "";
                            String ip = "";
                            String port = "";
                            String suffix = "";
                            if (dto.getSuffixUrl() != null && !dto.getSuffixUrl().trim().isEmpty()) {
                                if (dto.getSuffixUrl().trim().endsWith("/")) {
                                    suffix = dto.getSuffixUrl().substring(0, dto.getSuffixUrl().trim().length() - 1);
                                } else {
                                    suffix = dto.getSuffixUrl().trim();
                                }
                            }
                            if (dto.getUrl() != null && !dto.getUrl().trim().isEmpty()) {
                                // if valid -> pass
                                if (dto.getUrl().trim().endsWith("/")) {
                                    url = dto.getUrl().substring(0, dto.getUrl().trim().length() - 1);
                                } else {
                                    url = dto.getUrl().trim();
                                }
                            } else if (dto.getIp() != null && dto.getPort() != null
                                    && !dto.getIp().trim().isEmpty() && !dto.getPort().trim().isEmpty()) {
                                // if valid -> pass
                                ip = dto.getIp().trim();
                                port = dto.getPort().trim();
                            } else {
                                // if not -> show msg
                                logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                result = new ResponseMessageDTO("FAILED", "E87");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                            if (!url.trim().isEmpty() || (!ip.trim().isEmpty() && !port.trim().isEmpty())) {
                                // do insert customer sync
                                UUID uuid = UUID.randomUUID();
                                boolean active = false;
                                if (dto.getCustomerSyncActive() == 1) {
                                    active = true;
                                }
                                CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                                customerSyncEntity.setId(uuid.toString());
                                customerSyncEntity.setActive(active);
                                customerSyncEntity.setInformation(url);
                                customerSyncEntity.setIpAddress(ip);
                                customerSyncEntity.setPassword(dto.getCustomerPassword());
                                customerSyncEntity.setPort(port);
                                customerSyncEntity.setSuffixUrl(suffix);
                                customerSyncEntity.setToken("");
                                customerSyncEntity.setUserId("");
                                customerSyncEntity.setUsername(dto.getCustomerUsername());
                                customerSyncEntity.setMerchant(dto.getMerchantName());
                                customerSyncEntity.setAddress(dto.getAddress());
                                customerSyncService.insertCustomerSync(customerSyncEntity);
                                ///
                                // 7. add account_customer
                                UUID uuidAccountCustomer = UUID.randomUUID();
                                String systemPassword = encodeBase64(dto.getSystemUsername());
                                AccountCustomerEntity accountCustomerEntity = new AccountCustomerEntity();
                                accountCustomerEntity.setId(uuidAccountCustomer.toString());
                                accountCustomerEntity.setAvailable(true);
                                accountCustomerEntity.setPassword(systemPassword);
                                accountCustomerEntity.setRole("ROLE_USER");
                                accountCustomerEntity.setUsername(dto.getSystemUsername());
                                accountCustomerService.insert(accountCustomerEntity);
                                // 8. add account_customer_bank
                                UUID accountCustomerBankUUID = UUID.randomUUID();
                                AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                                String bankId = "";
                                if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                                    bankId = checkExistedBankAccount;
                                } else {
                                    bankId = uuidAccountBank.toString();
                                }
                                accountCustomerBankEntity.setId(accountCustomerBankUUID.toString());
                                accountCustomerBankEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                                accountCustomerBankEntity.setBankId(bankId);
                                accountCustomerBankEntity.setCustomerSyncId(uuid.toString());
                                accountCustomerBankService.insert(accountCustomerBankEntity);
                                result = new ResponseMessageDTO("SUCCESS", "");
                                httpStatus = HttpStatus.OK;
                            } else {
                                // if not -> show msg
                                logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                result = new ResponseMessageDTO("FAILED", "E87");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }

                        } else {

                            //////////
                            // prod:
                            // 5. check bankAccount existed and linked
                            String checkExistedBankAccount = accountBankReceiveService
                                    .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                            if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                                // if existed and linked -> pass
                                //
                                // 6. update is_sync = true into bank account
                                accountBankReceiveService.updateBankAccountSync(true, checkExistedBankAccount);
                                // 7. add customer_sync (with status)
                                String url = "";
                                String ip = "";
                                String port = "";
                                String suffix = "";
                                if (dto.getSuffixUrl() != null && !dto.getSuffixUrl().trim().isEmpty()) {
                                    if (dto.getSuffixUrl().trim().endsWith("/")) {
                                        suffix = dto.getSuffixUrl().substring(0,
                                                dto.getSuffixUrl().trim().length() - 1);
                                    } else {
                                        suffix = dto.getSuffixUrl().trim();
                                    }
                                }
                                if (dto.getUrl() != null && !dto.getUrl().trim().isEmpty()) {
                                    // if valid -> pass
                                    if (dto.getUrl().trim().endsWith("/")) {
                                        url = dto.getUrl().substring(0, dto.getUrl().trim().length() - 1);
                                    } else {
                                        url = dto.getUrl().trim();
                                    }
                                } else if (dto.getIp() != null && dto.getPort() != null
                                        && !dto.getIp().trim().isEmpty() && !dto.getPort().trim().isEmpty()) {
                                    // if valid -> pass
                                    ip = dto.getIp().trim();
                                    port = dto.getPort().trim();
                                } else {
                                    // if not -> show msg
                                    logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                    result = new ResponseMessageDTO("FAILED", "E87");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                                if (!url.trim().isEmpty() || (!ip.trim().isEmpty() && !port.trim().isEmpty())) {
                                    // do insert customer sync
                                    UUID uuid = UUID.randomUUID();
                                    boolean active = false;
                                    if (dto.getCustomerSyncActive() == 1) {
                                        active = true;
                                    }
                                    CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                                    customerSyncEntity.setId(uuid.toString());
                                    customerSyncEntity.setActive(active);
                                    customerSyncEntity.setInformation(url);
                                    customerSyncEntity.setIpAddress(ip);
                                    customerSyncEntity.setPassword(dto.getCustomerPassword());
                                    customerSyncEntity.setPort(port);
                                    customerSyncEntity.setSuffixUrl(suffix);
                                    customerSyncEntity.setToken("");
                                    customerSyncEntity.setUserId("");
                                    customerSyncEntity.setUsername(dto.getCustomerUsername());
                                    customerSyncEntity.setMerchant(dto.getMerchantName());
                                    customerSyncEntity.setAddress(dto.getAddress());
                                    customerSyncService.insertCustomerSync(customerSyncEntity);
                                    ///
                                    // 7. add account_customer
                                    UUID uuidAccountCustomer = UUID.randomUUID();
                                    String systemPassword = encodeBase64(dto.getSystemUsername());
                                    AccountCustomerEntity accountCustomerEntity = new AccountCustomerEntity();
                                    accountCustomerEntity.setId(uuidAccountCustomer.toString());
                                    accountCustomerEntity.setAvailable(true);
                                    accountCustomerEntity.setPassword(systemPassword);
                                    accountCustomerEntity.setRole("ROLE_USER");
                                    accountCustomerEntity.setUsername(dto.getSystemUsername());
                                    accountCustomerService.insert(accountCustomerEntity);
                                    // 8. add account_customer
                                    UUID accountCustomerBankUUID = UUID.randomUUID();
                                    AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                                    // 9. add account_customer_bank
                                    accountCustomerBankEntity.setId(accountCustomerBankUUID.toString());
                                    accountCustomerBankEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                    accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                                    accountCustomerBankEntity.setBankId(checkExistedBankAccount);
                                    accountCustomerBankEntity.setCustomerSyncId(uuid.toString());
                                    accountCustomerBankService.insert(accountCustomerBankEntity);
                                    result = new ResponseMessageDTO("SUCCESS", "");
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    // if not -> show msg
                                    logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                    result = new ResponseMessageDTO("FAILED", "E87");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }

                            } else {
                                // else -> show msg
                                logger.info("insertNewCustomerSync: BANK IS NOT LINKED INTO SYSTEM BEFORE.");
                                result = new ResponseMessageDTO("FAILED", "E84");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        }
                    }
                }
            } else {
                logger.error("insertNewCustomerSync: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("insertNewCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
