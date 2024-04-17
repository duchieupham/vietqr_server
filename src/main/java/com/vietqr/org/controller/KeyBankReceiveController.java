package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class KeyBankReceiveController {

    private static final Logger logger = Logger.getLogger(KeyBankReceiveController.class);

    @Autowired
    private KeyActiveBankReceiveService keyActiveBankReceiveService;

    @Autowired
    private BankReceiveOtpService bankReceiveOtpService;

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private BankReceiveActiveHistoryService bankReceiveActiveHistoryService;

    @Autowired
    private TrAnnualFeePackageService trAnnualFeePackageService;

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private TransactionWalletService transactionWalletService;

    @Autowired
    private TransactionReceiveService transactionReceiveService;

    @PostMapping("request-active-key")
    public ResponseEntity<Object> requestActiveBankReceive(
            @Valid @RequestBody RequestActiveBankReceiveDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("requestActiveBankReceive: request: " + dto.toString()
                    + " at: " + System.currentTimeMillis());
            //
            //1. check password chính xác hay chưa
            String checkPassword = accountLoginService.checkPassword(dto.getUserId(), dto.getPassword());
            if (checkPassword == null || checkPassword.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E55");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                //2. check bankId có tồn tại hay không và đã authenticated chưa (đã active chưa)
                BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
                        .checkBankReceiveActive(dto.getBankId());
                if (bankReceiveCheckDTO == null) {
                    result = new ResponseMessageDTO("FAILED", "E25");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!bankReceiveCheckDTO.getAuthenticated()) {
                    result = new ResponseMessageDTO("FAILED", "E101");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!Objects.equals(bankReceiveCheckDTO.getUserId(), dto.getUserId())) {
                    result = new ResponseMessageDTO("FAILED", "E126");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    LocalDateTime time = LocalDateTime.now();
                    long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                    //3. check key có valid không (đã tồn tại trong db chưa)
                    KeyActiveBankReceiveDTO keyActiveEntity = keyActiveBankReceiveService.checkKeyExist(dto.getKey());
                    if (keyActiveEntity == null) {
                        result = new ResponseMessageDTO("FAILED", "E127");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (!isKeyActive(dto.getKey(), keyActiveEntity.getSecretKey(), keyActiveEntity.getDuration(),
                            keyActiveEntity.getValueActive())) {
                        result = new ResponseMessageDTO("FAILED", "E131");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (keyActiveEntity.getStatus() != 0) {
                        result = new ResponseMessageDTO("FAILED", "E127");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        String otp = randomOtp();
                        KeyBankReceiveActiveDTO entity = accountBankReceiveService
                                .getAccountBankKeyById(dto.getBankId());
                        long validFeeFrom = entity.getValidFeeFrom();
                        long validFeeTo = entity.getValidFeeTo();
                        SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                        if (currentTime > systemSetting.getServiceActive()) {
                            if (bankReceiveCheckDTO.getIsValidService()) {
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, keyActiveEntity.getDuration());
                            } else {
                                validFeeFrom = currentTime;
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(currentTime, keyActiveEntity.getDuration());
                            }
                        } else {
                            if (bankReceiveCheckDTO.getIsValidService()) {
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, keyActiveEntity.getDuration());
                            } else {
                                validFeeFrom = systemSetting.getServiceActive();
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeFrom, keyActiveEntity.getDuration());
                            }
                        }
                        // save otp
                        BankReceiveOtpEntity bankReceiveOTPEntity = bankReceiveOtpService
                                .getBankReceiveOtpByKey(dto.getKey(), dto.getBankId(), dto.getUserId());
                        if (bankReceiveOTPEntity != null) {
                            bankReceiveOTPEntity.setOtpToken(otp);
                            bankReceiveOTPEntity.setExpiredDate(DateTimeUtil.plusMinuteAsLongInt(time, EnvironmentUtil.getMaximumExpiredMinutesOTP()));
                        } else {
                            bankReceiveOTPEntity = new BankReceiveOtpEntity();
                            bankReceiveOTPEntity.setId(UUID.randomUUID().toString());
                            bankReceiveOTPEntity.setBankId(dto.getBankId());
                            bankReceiveOTPEntity.setUserId(dto.getUserId());
                            bankReceiveOTPEntity.setOtpToken(otp);
                            bankReceiveOTPEntity.setExpiredDate(DateTimeUtil.plusMinuteAsLongInt(time, EnvironmentUtil.getMaximumExpiredMinutesOTP()));
                            bankReceiveOTPEntity.setStatus(0);
                            bankReceiveOTPEntity.setKeyActive(keyActiveEntity.getKeyActive());
                        }
                        bankReceiveOtpService.insert(bankReceiveOTPEntity);

                        // return response
                        RequestActiveBankResponseDTO responseDTO = new RequestActiveBankResponseDTO();
                        responseDTO.setKey(keyActiveEntity.getKeyActive());
                        responseDTO.setOtp(otp);
                        responseDTO.setDuration(keyActiveEntity.getDuration());
                        responseDTO.setValidFrom(validFeeFrom);
                        responseDTO.setValidTo(validFeeTo);

                        result = responseDTO;
                        httpStatus = HttpStatus.OK;
                    }
                }
            }
            //3. check key có valid không (đã tồn tại trong db chưa)
            //
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("requestActiveBankReceive: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("confirm-active-key")
    public ResponseEntity<ResponseMessageDTO> confirmActiveBankReceive(
            @Valid @RequestBody ConfirmActiveBankReceiveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("confirmActiveBankReceive: request: " + dto.toString() + " at: " + System.currentTimeMillis());
            //
            ObjectMapper mapper = new ObjectMapper();
            String checkPassword = accountLoginService.checkPassword(dto.getUserId(), dto.getPassword());
            if (checkPassword == null || checkPassword.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E55");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                //2. check bankId có tồn tại hay không và đã authenticated chưa (đã active chưa)
                BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
                        .checkBankReceiveActive(dto.getBankId());
                if (bankReceiveCheckDTO == null) {
                    result = new ResponseMessageDTO("FAILED", "E25");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!bankReceiveCheckDTO.getAuthenticated()) {
                    result = new ResponseMessageDTO("FAILED", "E101");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!Objects.equals(bankReceiveCheckDTO.getUserId(), dto.getUserId())) {
                    result = new ResponseMessageDTO("FAILED", "E126");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    LocalDateTime time = LocalDateTime.now();
                    long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                    BankReceiveOtpDTO bankReceiveOtpDTO = bankReceiveOtpService
                            .checkBankReceiveOtp(dto.getUserId(), dto.getBankId(), dto.getOtp(), dto.getKey());
                    if (bankReceiveOtpDTO == null) {
                        // otp sai hoặc ko tìm thấy key
                        result = new ResponseMessageDTO("FAILED", "E128");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (currentTime > bankReceiveOtpDTO.getExpiredDate()) {
                        result = new ResponseMessageDTO("FAILED", "E129");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        KeyActiveBankReceiveDTO keyActiveEntity = keyActiveBankReceiveService
                                .checkKeyExist(bankReceiveOtpDTO.getKeyActive());
                        if (keyActiveEntity == null) {
                            result = new ResponseMessageDTO("FAILED", "E127");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else if (keyActiveEntity.getStatus() != 0) {
                            result = new ResponseMessageDTO("FAILED", "E127");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else if (!isKeyActive(bankReceiveOtpDTO.getKeyActive(), keyActiveEntity.getSecretKey(),
                                keyActiveEntity.getDuration(), keyActiveEntity.getValueActive())) {
                            result = new ResponseMessageDTO("FAILED", "E131");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            int newVersion = keyActiveEntity.getVersion() + 1;
                            int isSuccess = keyActiveBankReceiveService
                                    .updateActiveKey(bankReceiveOtpDTO.getKeyActive(), keyActiveEntity.getVersion(), newVersion);
                            if (isSuccess == 1) {
                                long validFeeFrom = bankReceiveCheckDTO.getValidFrom();
                                long validFeeTo = bankReceiveCheckDTO.getValidTo();
                                // update validFeeFrom, validFeeTo
                                SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                                if (currentTime > systemSetting.getServiceActive()) {
                                    if (bankReceiveCheckDTO.getIsValidService()) {
                                        validFeeFrom = bankReceiveCheckDTO.getValidFrom();
                                        validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, keyActiveEntity.getDuration());
                                    } else {
                                        validFeeFrom = currentTime;
                                        validFeeTo = DateTimeUtil.plusMonthAsLongInt(currentTime, keyActiveEntity.getDuration());
                                    }
                                } else {
                                    if (bankReceiveCheckDTO.getIsValidService()) {
                                        validFeeFrom = bankReceiveCheckDTO.getValidFrom();
                                        validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, keyActiveEntity.getDuration());
                                    } else {
                                        validFeeFrom = systemSetting.getServiceActive();
                                        validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeFrom, keyActiveEntity.getDuration());
                                    }
                                }
                                // save history
                                BankReceiveActiveHistoryEntity bankReceiveActiveHistoryEntity = new BankReceiveActiveHistoryEntity();
                                bankReceiveActiveHistoryEntity.setId(UUID.randomUUID().toString());
                                bankReceiveActiveHistoryEntity.setKeyId(bankReceiveOtpDTO.getId());
                                bankReceiveActiveHistoryEntity.setType(0);
                                bankReceiveActiveHistoryEntity.setKeyActive(bankReceiveOtpDTO.getKeyActive());
                                bankReceiveActiveHistoryEntity.setBankId(dto.getBankId());
                                bankReceiveActiveHistoryEntity.setUserId(dto.getUserId());
                                bankReceiveActiveHistoryEntity.setCreateAt(currentTime);
                                bankReceiveActiveHistoryEntity.setValidFeeFrom(validFeeFrom);
                                bankReceiveActiveHistoryEntity.setValidFeeTo(validFeeTo);
                                bankReceiveActiveHistoryEntity.setData(mapper.writeValueAsString(dto));

                                bankReceiveOtpService.updateStatusBankReceiveOtp(bankReceiveOtpDTO.getId(), 1);
                                bankReceiveActiveHistoryService.insert(bankReceiveActiveHistoryEntity);
                                accountBankReceiveService.updateActiveBankReceive(dto.getBankId(), validFeeFrom, validFeeTo);
                                result = new ResponseMessageDTO("SUCCESS", "");
                                httpStatus = HttpStatus.OK;
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E130");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        }
                    }
                }
                //
                logger.info("confirmActiveBankReceive: response: STATUS: " + result.getStatus()
                        + ", MESSAGE: " + result.getMessage() + " at: " + System.currentTimeMillis());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("confirmActiveBankReceive: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("key/check-active")
    public ResponseEntity<Object> checkActiveKeyForAdmin(@RequestBody CheckActiveKeyBank dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("checkActiveKeyForAdmin: request: " + dto.toString() + " at: " + System.currentTimeMillis());
            //
            KeyActiveBankCheckDTO keyActiveBankCheckDTO = keyActiveBankReceiveService.checkKeyActiveByKey(dto.getKeyActive());
            if (keyActiveBankCheckDTO == null) {
                result = new ResponseMessageDTO("CHECK", "C01");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (keyActiveBankCheckDTO.getStatus() == 0) {

                KeyActiveAdminDTO activeAdminDTO = new KeyActiveAdminDTO();
                activeAdminDTO.setKey(keyActiveBankCheckDTO.getKeyActive());
                activeAdminDTO.setStatus(keyActiveBankCheckDTO.getStatus());
                activeAdminDTO.setDuration(keyActiveBankCheckDTO.getDuration());
                activeAdminDTO.setCreateAt(keyActiveBankCheckDTO.getCreateAt());
                activeAdminDTO.setValidKey(isKeyActive(keyActiveBankCheckDTO.getKeyActive(),
                        keyActiveBankCheckDTO.getSecretKey(), keyActiveBankCheckDTO.getDuration(),
                        keyActiveBankCheckDTO.getValueActive()));
                activeAdminDTO.setBankId("");
                activeAdminDTO.setBankAccount("");
                activeAdminDTO.setBankName("");
                activeAdminDTO.setBankShortName("");
                activeAdminDTO.setUserBankName("");
                activeAdminDTO.setValidFeeFrom(0);
                activeAdminDTO.setValidFeeTo(0);
                activeAdminDTO.setUserId("");
                activeAdminDTO.setFullName("");
                activeAdminDTO.setPhoneNo("");
                activeAdminDTO.setEmail("");

                result = activeAdminDTO;
                httpStatus = HttpStatus.OK;
            } else if (keyActiveBankCheckDTO.getStatus() == 1) {
                KeyActiveAdminDTO activeAdminDTO = new KeyActiveAdminDTO();
                activeAdminDTO.setKey(keyActiveBankCheckDTO.getKeyActive());
                activeAdminDTO.setStatus(keyActiveBankCheckDTO.getStatus());
                activeAdminDTO.setDuration(keyActiveBankCheckDTO.getDuration());
                activeAdminDTO.setCreateAt(keyActiveBankCheckDTO.getCreateAt());
                activeAdminDTO.setValidKey(isKeyActive(keyActiveBankCheckDTO.getKeyActive(),
                        keyActiveBankCheckDTO.getSecretKey(), keyActiveBankCheckDTO.getDuration(),
                        keyActiveBankCheckDTO.getValueActive()));
                BankActiveAdminDataDTO bankActiveAdminDataDTO = bankReceiveActiveHistoryService
                        .getBankActiveAdminData(keyActiveBankCheckDTO.getKeyActive());
                if (bankActiveAdminDataDTO != null) {
                    activeAdminDTO.setBankId(bankActiveAdminDataDTO.getBankId());
                    activeAdminDTO.setBankAccount(bankActiveAdminDataDTO.getBankAccount());
                    activeAdminDTO.setBankName(bankActiveAdminDataDTO.getBankName());
                    activeAdminDTO.setBankShortName(bankActiveAdminDataDTO.getBankShortName());
                    activeAdminDTO.setUserBankName(bankActiveAdminDataDTO.getUserBankName());
                    activeAdminDTO.setValidFeeFrom(bankActiveAdminDataDTO.getValidFeeFrom());
                    activeAdminDTO.setValidFeeTo(bankActiveAdminDataDTO.getValidFeeTo());
                    activeAdminDTO.setUserId(bankActiveAdminDataDTO.getUserId());
                    activeAdminDTO.setFullName(bankActiveAdminDataDTO.getFullName());
                    activeAdminDTO.setPhoneNo(bankActiveAdminDataDTO.getPhoneNo());
                    activeAdminDTO.setEmail(bankActiveAdminDataDTO.getEmail());
                } else {
                    activeAdminDTO.setBankId("");
                    activeAdminDTO.setBankAccount("");
                    activeAdminDTO.setBankName("");
                    activeAdminDTO.setBankShortName("");
                    activeAdminDTO.setUserBankName("");
                    activeAdminDTO.setValidFeeFrom(0);
                    activeAdminDTO.setValidFeeTo(0);
                    activeAdminDTO.setUserId("");
                    activeAdminDTO.setFullName("");
                    activeAdminDTO.setPhoneNo("");
                    activeAdminDTO.setEmail("");
                }

                result = activeAdminDTO;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("CHECK", "C01");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("checkActiveKeyForAdmin: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("key-active-bank/generate-key")
    public ResponseEntity<Object> generateKeyForAdmin(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody GenerateKeyBankDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            if (username != null && !username.trim().isEmpty() && username.equals(EnvironmentUtil.getAdminUatActiveKey())) {
                logger.info("generateKeyForAdmin: request: " + dto.toString() + " at: " + System.currentTimeMillis());
                //
                List<String> keyActives = generateKeyActiveWithCheckDuplicated(dto.getNumOfKeys());
                List<KeyActiveBankReceiveEntity> entities = new ArrayList<>();
                LocalDateTime time = LocalDateTime.now();
                long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                for (String keyActive : keyActives) {
                    KeyActiveBankReceiveEntity entity = new KeyActiveBankReceiveEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setKeyActive(keyActive);
                    entity.setSecretKey(generateSecretKey());
                    entity.setValueActive(generateValueActive(keyActive, entity.getSecretKey(), dto.getDuration()));
                    entity.setDuration(dto.getDuration());
                    entity.setCreateAt(currentTime);
                    entity.setVersion(0);
                    entity.setStatus(0);
                    entities.add(entity);
                }
                keyActiveBankReceiveService.insertAll(entities);
                result = keyActives;
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("generateKeyForAdmin: unknown generated-key: token: " + token + " at: " + System.currentTimeMillis());
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("generateKeyForAdmin: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("key-active-bank/annual-fee")
    public ResponseEntity<List<TrAnnualFeeDTO>> getAnnualFeePackage() {
        List<TrAnnualFeeDTO> result = null;
        HttpStatus httpStatus = null;
        try {
            result = trAnnualFeePackageService.getAllFee();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("getAnnualFeePackage: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ArrayList<>();
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // tạo qr
    // nạp tiền vào ví
    // check tiền tu account mới
    // trừ tiền từ ví
    // tạo thành công
    @PostMapping("qr-active-bank/request-active")
    public ResponseEntity<Object> keyActiveWithPaymentMethod(@Valid @RequestBody KeyActiveQrPaymentDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String checkPassword = accountLoginService.checkPassword(dto.getUserId(), dto.getPassword());
            if (checkPassword == null || checkPassword.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E55");
                httpStatus = HttpStatus.BAD_REQUEST;
                // password correct
            } else {
                //2. check bankId có tồn tại hay không và đã authenticated chưa (đã active chưa)
                BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
                        .checkBankReceiveActive(dto.getBankId());
                // ko tim thaays bankId
                if (bankReceiveCheckDTO == null) {
                    result = new ResponseMessageDTO("FAILED", "E25");
                    httpStatus = HttpStatus.BAD_REQUEST;
                // bankId chưa active
                } else if (!bankReceiveCheckDTO.getAuthenticated()) {
                    result = new ResponseMessageDTO("FAILED", "E101");
                    httpStatus = HttpStatus.BAD_REQUEST;
                // bankId khong thuoc userId
                } else if (!Objects.equals(bankReceiveCheckDTO.getUserId(), dto.getUserId())) {
                    result = new ResponseMessageDTO("FAILED", "E126");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    LocalDateTime time = LocalDateTime.now();
                    long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                    TrAnnualFeeDTO feeDTO = trAnnualFeePackageService.getFeeById(dto.getFeeId());
                    if (feeDTO == null) {
                        result = new ResponseMessageDTO("FAILED", "E132");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        String requestId = UUID.randomUUID().toString();
                        String otp = randomOtp();
//                        KeyBankReceiveActiveDTO entity = accountBankReceiveService
//                                .getAccountBankKeyById(dto.getBankId());
                        long validFeeFrom = 0;
                        long validFeeTo = bankReceiveCheckDTO.getValidTo();
                        SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                        if (currentTime > systemSetting.getServiceActive()) {
                            if (bankReceiveCheckDTO.getIsValidService()) {
                                validFeeFrom = bankReceiveCheckDTO.getValidFrom();
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, feeDTO.getDuration());
                            } else {
                                validFeeFrom = currentTime;
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(currentTime, feeDTO.getDuration());
                            }
                        } else {
                            if (bankReceiveCheckDTO.getIsValidService()) {
                                validFeeFrom = bankReceiveCheckDTO.getValidFrom();
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, feeDTO.getDuration());
                            } else {
                                validFeeFrom = systemSetting.getServiceActive();
                                validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeFrom, feeDTO.getDuration());
                            }
                        }
                        // save otp
                        BankReceiveOtpEntity bankReceiveOTPEntity = new BankReceiveOtpEntity();
                        bankReceiveOTPEntity.setId(UUID.randomUUID().toString());
                        bankReceiveOTPEntity.setBankId(dto.getBankId());
                        bankReceiveOTPEntity.setUserId(dto.getUserId());
                        bankReceiveOTPEntity.setOtpToken(otp);
                        bankReceiveOTPEntity
                                .setExpiredDate(DateTimeUtil.plusMinuteAsLongInt(time,
                                        EnvironmentUtil.getMaximumExpiredMinutesOTP()));
                        bankReceiveOTPEntity.setStatus(0);
                        bankReceiveOTPEntity.setKeyActive(feeDTO.getFeeId());
                        bankReceiveOTPEntity.setRequestId(requestId);
                        bankReceiveOTPEntity.setAmount(feeDTO.getAmount());
                        bankReceiveOtpService.insert(bankReceiveOTPEntity);

                        // create transaction_wallet
                        TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                        UUID transWalletUUID = UUID.randomUUID();
                        String billNumber = "VAF" + RandomCodeUtil.generateRandomId(10);
                        String otpPayment = RandomCodeUtil.generateOTP(6);
                        transactionWalletEntity.setId(transWalletUUID.toString());
                        transactionWalletEntity.setAmount("0");
                        transactionWalletEntity.setBillNumber(billNumber);
                        transactionWalletEntity.setContent("");
                        transactionWalletEntity.setStatus(0);
                        transactionWalletEntity.setTimeCreated(currentTime);
                        transactionWalletEntity.setTimePaid(0);
                        transactionWalletEntity.setTransType("D");
                        transactionWalletEntity.setUserId(dto.getUserId());
                        transactionWalletEntity.setOtp(otpPayment);
                        transactionWalletEntity.setPaymentType(2);
                        transactionWalletEntity.setPaymentMethod(0);
                        transactionWalletEntity.setReferenceNumber("");
                        transactionWalletEntity.setPhoneNoRC("");
                        transactionWalletService
                                .insertTransactionWallet(transactionWalletEntity);

                        // return response
                        RequestActiveBankQrResponseDTO responseDTO = new RequestActiveBankQrResponseDTO();
                        responseDTO.setRequest(requestId);
                        responseDTO.setOtp(otp);
                        responseDTO.setDuration(feeDTO.getDuration());
                        responseDTO.setValidFrom(validFeeFrom);
                        responseDTO.setValidTo(validFeeTo);
                        responseDTO.setFeeId(dto.getFeeId());
                        responseDTO.setOtpPayment(otpPayment);
                        result = responseDTO;
                        httpStatus = HttpStatus.OK;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("keyActiveWithPaymentMethod: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("qr-active-bank/confirm-active")
    public ResponseEntity<Object> confirmQrActiveBankReceive(
            @Valid @RequestBody ConfirmActiveQrBankDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("confirmQrActiveBankReceive: request: " + dto.toString() + " at: " + System.currentTimeMillis());
            //
            String checkPassword = accountLoginService.checkPassword(dto.getUserId(), dto.getPassword());
            if (checkPassword == null || checkPassword.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E55");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                //2. check bankId có tồn tại hay không và đã authenticated chưa (đã active chưa)
                BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
                        .checkBankReceiveActive(dto.getBankId());
                if (bankReceiveCheckDTO == null) {
                    result = new ResponseMessageDTO("FAILED", "E25");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!bankReceiveCheckDTO.getAuthenticated()) {
                    result = new ResponseMessageDTO("FAILED", "E101");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!Objects.equals(bankReceiveCheckDTO.getUserId(), dto.getUserId())) {
                    result = new ResponseMessageDTO("FAILED", "E126");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    LocalDateTime time = LocalDateTime.now();
                    long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                    BankReceiveOtpDTO bankReceiveOtpDTO = bankReceiveOtpService
                            .checkBankReceiveOtp(dto.getUserId(), dto.getBankId(),
                                    dto.getOtp(), dto.getFeeId());

                    if (bankReceiveOtpDTO == null) {
                        // otp sai hoặc ko tìm thấy key
                        result = new ResponseMessageDTO("FAILED", "E128");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (currentTime > bankReceiveOtpDTO.getExpiredDate()) {
                        result = new ResponseMessageDTO("FAILED", "E129");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        TrAnnualFeeDTO feeDTO = trAnnualFeePackageService.getFeeById(dto.getFeeId());
                        if (feeDTO == null) {
                            result = new ResponseMessageDTO("FAILED", "E132");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            // tạo mã QR
                            if (dto.getPaymentMethod() == null || dto.getPaymentMethod() == 0) {
                                result = new ResponseMessageDTO("FAILED", "E70");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            } else if (dto.getPaymentMethod() == 1) {
                                String check = transactionWalletService
                                        .checkExistedTransactionnWallet(dto.getOtpPayment(), dto.getUserId(), 2);
                                if (check != null && !check.trim().isEmpty()) {
                                    UUID transReceiveUUID = UUID.randomUUID();
                                    UUID transWalletVQRUUID = UUID.randomUUID();
                                    long amount = (long) feeDTO.getAmount() * feeDTO.getDuration();
                                    //
                                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                                    String billNumberVQR = "VAF" + RandomCodeUtil.generateRandomId(10);
                                    String content = traceId + " " + billNumberVQR;
                                    // get bank recharge information
                                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                                    String bankId = EnvironmentUtil.getBankIdRecharge();
                                    String bankLogo = EnvironmentUtil.getBankLogoIdRecharge();
                                    String cai = EnvironmentUtil.getCAIRecharge();
                                    String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
                                    // generate VQR
                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                    vietQRGenerateDTO.setCaiValue(cai);
                                    vietQRGenerateDTO.setBankAccount(bankAccount);
                                    vietQRGenerateDTO.setAmount(amount + "");
                                    vietQRGenerateDTO.setContent(content);
                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                    // insert transaction_receive
                                    // insert transaction_receive
                                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                                    transactionReceiveEntity.setAmount(amount);
                                    transactionReceiveEntity.setBankAccount(bankAccount);
                                    transactionReceiveEntity.setBankId(bankId);
                                    transactionReceiveEntity.setContent(content);
                                    transactionReceiveEntity.setRefId("");
                                    transactionReceiveEntity.setStatus(0);
                                    transactionReceiveEntity.setTime(currentTime);
                                    transactionReceiveEntity.setType(5);
                                    transactionReceiveEntity.setTraceId(traceId);
                                    transactionReceiveEntity.setTransType("C");
                                    transactionReceiveEntity.setReferenceNumber("");
                                    transactionReceiveEntity.setOrderId(billNumberVQR);
                                    transactionReceiveEntity.setSign("");
                                    transactionReceiveEntity.setCustomerBankAccount("");
                                    transactionReceiveEntity.setCustomerBankCode("");
                                    transactionReceiveEntity.setCustomerName("");
                                    transactionReceiveEntity.setTerminalCode("");
                                    transactionReceiveEntity.setUserId(userIdHost);
                                    transactionReceiveEntity.setNote("");
                                    transactionReceiveEntity.setTransStatus(0);
                                    transactionReceiveEntity.setQrCode(qr);
                                    transactionReceiveEntity.setUrlLink("");
                                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                                    TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                                    transactionWalletEntity.setId(transWalletVQRUUID.toString());
                                    transactionWalletEntity.setAmount(amount + "");
                                    transactionWalletEntity.setBillNumber(billNumberVQR);
                                    transactionWalletEntity.setContent(content);
                                    transactionWalletEntity.setStatus(0);
                                    transactionWalletEntity.setTimeCreated(currentTime);
                                    transactionWalletEntity.setTimePaid(0);
                                    transactionWalletEntity.setTransType("C");
                                    transactionWalletEntity.setUserId(dto.getUserId());
                                    transactionWalletEntity.setOtp("");
                                    transactionWalletEntity.setPaymentType(0);
                                    transactionWalletEntity.setPaymentMethod(1);
                                    transactionWalletEntity
                                            .setReferenceNumber(2 + "*" + dto.getFeeId() + "*"
                                                    + dto.getUserId() + "*"
                                                    + dto.getOtpPayment() + "*" + dto.getBankId()
                                                    + "*" + dto.getOtp());
                                    transactionWalletEntity.setPhoneNoRC("");
                                    transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                                    // update transaction_wallet mobile recharge
                                    LocalDateTime currentDateTimeMobile = LocalDateTime.now();
                                    long timeMobile = currentDateTimeMobile.toEpochSecond(ZoneOffset.UTC);
                                    transactionWalletService.updateTransactionWalletConfirm(timeMobile, amount + "",
                                            dto.getUserId(), dto.getOtpPayment(), 2);
                                    // response billNumber*imgId*QR
                                    ConfirmPaymentActiveKeyDTO responseDTO = new ConfirmPaymentActiveKeyDTO();
                                    responseDTO.setBankLogo(bankLogo);
                                    responseDTO.setBillNumber(billNumberVQR);
                                    responseDTO.setQr(qr);
                                    responseDTO.setAmount(amount);
                                    result = responseDTO;
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    logger.error("rechargeMobile: INVALID OTP");
                                    result = new ResponseMessageDTO("FAILED", "E65");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                logger.error("rechargeMobile: INVALID PAYMENT METHOD");
                                result = new ResponseMessageDTO("FAILED", "E70");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        }
                    }
                }
                //
                logger.info("confirmActiveBankReceive: response: STATUS: " + httpStatus +
                        ", MESSAGE: " + result +
                        " at: " + System.currentTimeMillis());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("confirmActiveBankReceive: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String randomOtp() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
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
        }
        return result;
    }

    private String generateSecretKey() {
        return UUID.randomUUID().toString();
    }

    private String generateValueActive(String keyActive, String secretKey, int duration) {
        return BcryptKeyUtil.hashKeyActive(keyActive, secretKey, duration);
    }

    private List<String> generateKeyActiveWithCheckDuplicated(int numOfKeys) {
        List<String> keys = new ArrayList<>();
        keys = generateMultikeyActive(numOfKeys);
        // check duplicated;
        List<String> keysDuplicated = new ArrayList<>();
        do {
            keysDuplicated = keyActiveBankReceiveService.checkDuplicatedKeyActives(keys);
            if (keysDuplicated.isEmpty()) {
                break;
            }
            // remove duplicated
            for (String key : keysDuplicated) {
                keys.remove(key);
            }
            // generate new key
            List<String> newKeys = generateMultikeyActive(keysDuplicated.size());
            keys.addAll(newKeys);
        } while (!keysDuplicated.isEmpty());
        return keys;
    }

    private List<String> generateMultikeyActive(int numOfKeys) {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < numOfKeys; i++) {
            String key = generateKeyActive();
            keys.add(key);
        }
        return new ArrayList<>(keys);
    }

    private String generateKeyActive() {
        Random random = new Random();
        int length = EnvironmentUtil.getLengthKeyActiveBank();
        String characters = EnvironmentUtil.getCharactersKeyActiveBank();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private boolean isKeyActive(String keyActive, String secretKey, int duration, String valueActive) {
        String data = BcryptKeyUtil.hashKeyActive(keyActive, secretKey, duration);
        return data.equals(valueActive);
    }
}
