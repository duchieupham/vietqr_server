package com.vietqr.org.controller;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.VietQRUtil;
import com.vietqr.org.dto.RequestPaymentDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransWalletInsertDTO;
import com.vietqr.org.dto.TransWalletListDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionWalletEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionWalletController {
    private static final Logger logger = Logger.getLogger(TransactionWalletController.class);

    @Autowired
    TransactionWalletService transactionWalletService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @PostMapping("transaction-wallet/request-payment")
    public ResponseEntity<ResponseMessageDTO> requestPayment(@RequestBody RequestPaymentDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String checkExisted = accountLoginService.checkExistedUserByIdAndPassword(dto.getUserId(),
                        dto.getPassword());
                if (checkExisted != null && !checkExisted.trim().isEmpty()) {
                    // 1. check payment type
                    // 2.1 if = 0. now ignore
                    // 2.2 if = 1. do
                    // 2.2.1 do insert transaction wallet => type = 0 & otp & payment type = 1
                    // 2.2.2 response otp
                    // 2.3 if = 2. now ignore
                    // 2.4 if != 1|2|3. response error
                    if (dto.getPaymentType() == 0) {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (dto.getPaymentType() == 1) {
                        // insert transaction_wallet
                        UUID transWalletUUID = UUID.randomUUID();
                        String billNumber = "VPM" + RandomCodeUtil.generateRandomId(10);
                        String otp = RandomCodeUtil.generateOTP(6);
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                        transactionWalletEntity.setId(transWalletUUID.toString());
                        transactionWalletEntity.setAmount("0");
                        transactionWalletEntity.setBillNumber(billNumber);
                        transactionWalletEntity.setContent("");
                        transactionWalletEntity.setStatus(0);
                        transactionWalletEntity.setTimeCreated(time);
                        transactionWalletEntity.setTimePaid(0);
                        transactionWalletEntity.setTransType("D");
                        transactionWalletEntity.setUserId(dto.getUserId());
                        transactionWalletEntity.setOtp(otp);
                        transactionWalletEntity.setPaymentType(dto.getPaymentType());
                        transactionWalletEntity.setPaymentMethod(0);
                        transactionWalletEntity.setReferenceNumber("");
                        transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                        //
                        result = new ResponseMessageDTO("SUCCESS", otp);
                        httpStatus = HttpStatus.OK;
                    } else if (dto.getPaymentType() == 2) {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("requestPayment: USER NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E55");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("requestPayment: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("requestPayment: Error " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction-wallet")
    public ResponseEntity<Object> insertTransactionWallet(@RequestBody TransWalletInsertDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String userId = accountLoginService.getUserIdByPhoneNo(dto.getPhoneNo());
                if (userId != null) {
                    UUID transReceiveUUID = UUID.randomUUID();
                    UUID transcationReceiveBranchUUID = UUID.randomUUID();
                    UUID transWalletUUID = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    //
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumber = "VRC" + RandomCodeUtil.generateRandomId(10);
                    String content = traceId + " " + billNumber;
                    // get bank recharge information
                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                    String bankId = EnvironmentUtil.getBankIdRecharge();
                    String bankLogo = EnvironmentUtil.getBankLogoIdRecharge();
                    String cai = EnvironmentUtil.getCAIRecharge();
                    String businessId = EnvironmentUtil.getBusinessIdRecharge();
                    String branchId = EnvironmentUtil.getBranchIdRecharge();
                    // create VietQR

                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(cai);
                    vietQRGenerateDTO.setBankAccount(bankAccount);
                    vietQRGenerateDTO.setAmount(dto.getAmount());
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    // generate VietQRDTO
                    VietQRDTO vietQRDTO = new VietQRDTO();
                    vietQRDTO.setBankCode("");
                    vietQRDTO.setBankName("");
                    vietQRDTO.setBankAccount("");
                    vietQRDTO.setUserBankName("");
                    vietQRDTO.setAmount(dto.getAmount());
                    vietQRDTO.setContent(content);
                    vietQRDTO.setQrCode(qr);
                    vietQRDTO.setImgId(bankLogo);
                    // return transactionId to upload bill image
                    vietQRDTO.setTransactionId("");

                    // insert transaction_receive
                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                    transactionReceiveEntity.setAmount(Long.parseLong(dto.getAmount()));
                    transactionReceiveEntity.setBankAccount(bankAccount);
                    transactionReceiveEntity.setBankId(bankId);
                    transactionReceiveEntity.setContent(content);
                    transactionReceiveEntity.setRefId("");
                    transactionReceiveEntity.setStatus(0);
                    transactionReceiveEntity.setTime(time);
                    transactionReceiveEntity.setType(5);
                    transactionReceiveEntity.setTraceId(traceId);
                    transactionReceiveEntity.setTransType("C");
                    transactionReceiveEntity.setReferenceNumber("");
                    transactionReceiveEntity.setOrderId(billNumber);
                    transactionReceiveEntity.setSign("");
                    transactionReceiveEntity.setCustomerBankAccount("");
                    transactionReceiveEntity.setCustomerBankCode("");
                    transactionReceiveEntity.setCustomerName("");
                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                    // insert transaction branch
                    if (businessId != null && branchId != null && !businessId.trim().isEmpty()
                            && !branchId.trim().isEmpty()) {
                        TransactionReceiveBranchEntity transactionReceiveBranchEntity = new TransactionReceiveBranchEntity();
                        transactionReceiveBranchEntity.setId(transcationReceiveBranchUUID.toString());
                        transactionReceiveBranchEntity.setBusinessId(businessId);
                        transactionReceiveBranchEntity.setBranchId(branchId);
                        transactionReceiveBranchEntity.setTransactionReceiveId(transReceiveUUID.toString());
                        transactionReceiveBranchService.insertTransactionReceiveBranch(transactionReceiveBranchEntity);
                    }
                    // insert transaction_wallet
                    TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                    transactionWalletEntity.setId(transWalletUUID.toString());
                    transactionWalletEntity.setAmount(dto.getAmount());
                    transactionWalletEntity.setBillNumber(billNumber);
                    transactionWalletEntity.setContent(content);
                    transactionWalletEntity.setStatus(0);
                    transactionWalletEntity.setTimeCreated(time);
                    transactionWalletEntity.setTimePaid(0);
                    transactionWalletEntity.setTransType("C");
                    transactionWalletEntity.setUserId(userId);
                    transactionWalletEntity.setOtp("");
                    transactionWalletEntity.setPaymentType(0);
                    transactionWalletEntity.setPaymentMethod(1);
                    transactionWalletEntity.setReferenceNumber("");
                    transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                    // final
                    result = vietQRDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("insertTransactionWallet: USER NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E59");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertTransactionWallet: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertTransactionWallet: Error " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;

        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update giao dich thanh cong => transaction sync xu ly

    // get list all

    @GetMapping("transaction-wallet")
    public ResponseEntity<List<TransWalletListDTO>> getTransactionWalletsFilter(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset) {
        List<TransWalletListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (status == 9) {
                result = transactionWalletService.getTransactionWalletList(userId, offset);
            } else {
                result = transactionWalletService.getTransactionWalletListByStatus(userId, status, offset);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionWalletsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list by userId

    // get chi tiết

}
