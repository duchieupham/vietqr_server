package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantSyncController {

    private static final Logger logger = Logger.getLogger(MerchantSyncController.class);

    @Autowired
    private MerchantSyncService merchantSyncService;

    @Autowired
    private CustomerSyncService customerSyncService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private TerminalBankService terminalBankService;

    @Autowired
    private AccountCustomerService accountCustomerService;

    @Autowired
    private BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    private SocketHandler socketHandler;

    @GetMapping("merchant-sync")
    public ResponseEntity<Object> getAllMerchants(
            @RequestParam(value = "value", defaultValue = "") String value,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();

        try {
            int totalElement = merchantSyncService.countMerchantsByName(value);
            int offset = (page - 1) * size;
            List<IMerchantSyncDTO> data = merchantSyncService.getAllMerchants(value, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("ecommerce")
    public ResponseEntity<Object> syncEcommerce(@RequestBody EcommerceMerchantSyncDTO dto,
                                                @RequestHeader("Authorization") String token) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            if (!StringUtil.isNullOrEmpty(username)) {
                String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                if (!StringUtil.isNullOrEmpty(accessKey)) {
                    String checkSum = BankEncryptUtil.generateMD5EcommerceCheckSum(accessKey, dto.getEcommerceSite());
                    if (dto.getCheckSum().equals(checkSum)) {
                        CustomerSyncEntity entity = new CustomerSyncEntity();
                        UUID uuid = UUID.randomUUID();
                        entity.setId(uuid.toString());
                        entity.setUsername("");
                        entity.setPassword("");
                        entity.setIpAddress("");
                        entity.setPort("");
                        entity.setSuffixUrl("");
                        entity.setInformation(dto.getEcommerceSite());
                        entity.setUserId("");
                        entity.setActive(false);
                        entity.setToken("");
                        entity.setMerchant("");
                        entity.setAddress("");
                        entity.setMaster(false);
                        entity.setAccountId("");
                        entity.setRefId("");
                        String publishId = "MER" + RandomCodeUtil.generateOTP(8);
                        String certificate = "MER-ECM-" + publishId;
                        String clientId = BoxTerminalRefIdUtil.encryptQrBoxId(uuid.toString());
                        MerchantSyncEntity merchantSyncEntity = new MerchantSyncEntity(uuid.toString(),
                                "", "", "Ecommerce wordpress", publishId, certificate,
                                StringUtil.getValueNullChecker(dto.getWebhook()), clientId);

                        customerSyncService.insertCustomerSync(entity);
                        merchantSyncService.insert(merchantSyncEntity);
                        result = new MerchantSyncEcommerceDTO(StringUtil.getValueNullChecker(dto.getWebhook()), clientId, certificate);
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E74");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getUsernameFromToken(String token) {
        String result = "";
        try {
            if (token != null && !token.trim().isEmpty()) {
                String secretKey = "mySecretKey";
                String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                String userId = (String) claims.get("user");
                if (userId != null) {
                    result = new String(Base64.getDecoder().decode(userId));
                }
            }
        } catch (Exception e) {
            logger.info("TerminalSyncController: ERROR: getUsernameFromToken: "
                    + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    @PostMapping("ecommerce/test")
    public ResponseEntity<Object> syncEcommerceTet(@RequestParam String clientId, @RequestBody EcommerceActiveDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
                Map<String, String> data = new HashMap<>();
                data.put("notificationType", NotificationUtil.getNotiSyncEcommerce());
                data.put("bankAccount", dto.getBankAccount());
                data.put("userBankName", dto.getUserBankName());
                data.put("bankCode", dto.getBankCode());
                data.put("ecommerceSite", dto.getMerchantName());
                socketHandler.sendMessageToClientId(clientId,
                        data);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("ecommerce/active")
    public ResponseEntity<Object> syncEcommerce(@RequestBody EcommerceActiveDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantSyncEntity entity = merchantSyncService.getMerchantSyncByCertificate(dto.getCertificate());
            if (Objects.nonNull(entity)) {
                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                        .getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
                if (Objects.nonNull(accountBankReceiveEntity)) {
                    entity.setFullName(dto.getFullName());
                    entity.setNationalId(dto.getNationalId());
                    entity.setEmail(dto.getEmail());
                    entity.setPhoneNo(dto.getPhoneNo());
                    entity.setAddress(dto.getAddress());
                    entity.setWebhook(dto.getWebhook());
                    entity.setCareer(dto.getCareer());

                    BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                    bankReceiveConnectionEntity.setId(UUID.randomUUID().toString());
                    if (accountBankReceiveEntity.isMmsActive()) {
                        TerminalBankEntity terminalBankEntity = terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                        bankReceiveConnectionEntity.setTerminalBankId(Objects.nonNull(terminalBankEntity) ? terminalBankEntity.getId() : "");
                    }
                    bankReceiveConnectionEntity.setActive(true);
                    bankReceiveConnectionEntity.setMidConnectId("");
                    bankReceiveConnectionEntity.setBankId(accountBankReceiveEntity.getId());
                    bankReceiveConnectionEntity.setData("[]");
                    bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                    merchantSyncService.insert(entity);
                    httpStatus = HttpStatus.OK;
                }
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-sync/{id}")
    public ResponseEntity<Object> getMerchantById(@PathVariable String id) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            IMerchantSyncDTO data = merchantSyncService.getMerchantById(id);
            if (data != null) {
                httpStatus = HttpStatus.OK;
                result = data;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
                result = new ResponseMessageDTO("FAILED", "Merchant not found");
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("merchant-sync")
    public ResponseEntity<Object> createMerchant(@RequestBody MerchantSyncRequestDTO dto,
                                                 @RequestParam String platform,
                                                 @RequestParam String details) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            if (("Google Chat".equals(platform) || "Lark".equals(platform)) && !isValidUrl(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Webhook không hợp lệ"), HttpStatus.BAD_REQUEST);
            } else if ("Telegram".equals(platform) && !isValidChatId(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Chat ID không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            MerchantSyncEntity entity = new MerchantSyncEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setName(dto.getName());
            entity.setVso(dto.getVso());
            entity.setBusinessType(dto.getBusinessType());
            entity.setCareer(dto.getCareer());
            entity.setAddress(dto.getAddress());
            entity.setNationalId(dto.getNationalId());
            entity.setUserId(dto.getUserId());
            entity.setEmail(dto.getEmail());
            entity.setIsActive(false);
            entity.setAccountCustomerId("");

            // Save entity
            MerchantSyncEntity createdEntity = merchantSyncService.createMerchant(entity);
            merchantSyncService.savePlatformDetails(platform, createdEntity.getUserId(), details);

            httpStatus = HttpStatus.CREATED;
            result = createdEntity;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    private boolean isValidChatId(String chatId) {
        return chatId != null && !chatId.trim().isEmpty();
    }
    @DeleteMapping("merchant-sync/{id}")
    public ResponseEntity<Object> deleteMerchant(@PathVariable String id) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            merchantSyncService.deleteMerchant(id);
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "Merchant deleted successfully");
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    @PutMapping("merchant-sync/{id}")
    public ResponseEntity<Object> updateMerchant(@PathVariable String id,
                                                 @RequestBody MerchantSyncRequestDTO dto,
                                                 @RequestParam String platform,
                                                 @RequestParam String details) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            if (("Google Chat".equals(platform) || "Lark".equals(platform)) && !isValidUrl(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Webhook không hợp lệ"), HttpStatus.BAD_REQUEST);
            } else if ("Telegram".equals(platform) && !isValidChatId(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Chat ID không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            Optional<MerchantSyncEntity> optionalEntity = merchantSyncService.findById(id);
            if (!optionalEntity.isPresent()) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Merchant not found"), HttpStatus.NOT_FOUND);
            }

            MerchantSyncEntity entity = optionalEntity.get();
            entity.setName(dto.getName());
            entity.setVso(dto.getVso());
            entity.setBusinessType(dto.getBusinessType());
            entity.setCareer(dto.getCareer());
            entity.setAddress(dto.getAddress());
            entity.setNationalId(dto.getNationalId());
            entity.setUserId(dto.getUserId());
            entity.setEmail(dto.getEmail());

            MerchantSyncEntity updatedEntity = merchantSyncService.updateMerchant(id, entity);
            merchantSyncService.savePlatformDetails(platform, updatedEntity.getUserId(), details);

            httpStatus = HttpStatus.OK;
            result = updatedEntity;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
