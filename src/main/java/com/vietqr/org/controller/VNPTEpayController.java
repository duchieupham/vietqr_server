package com.vietqr.org.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.EpayBalanceDTO;
import com.vietqr.org.dto.MobileRechargeDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.CarrierTypeEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionWalletEntity;
import com.vietqr.org.service.AccountWalletService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CarrierTypeService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.service.vnpt.services.QueryBalanceResult;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.FormatUtil;
import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.SocketHandler;
import com.vietqr.org.util.VNPTEpayUtil;
import com.vietqr.org.util.VietQRUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VNPTEpayController {
    private static final Logger logger = Logger.getLogger(VNPTEpayController.class);

    @Autowired
    CarrierTypeService carrierTypeService;

    @Autowired
    AccountWalletService accountWalletService;

    @Autowired
    TransactionWalletService transactionWalletService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    private SocketHandler socketHandler;

    // GET QR RECHARGE VNPT EPAY
    // Default Bank information:
    // Vietcombank: 0011002572864
    // CÔNG TY CỔ PHẦN THANH TOÁN ĐIỆN TỬ VNPT
    @GetMapping("epay/request-payment-qr")
    public ResponseEntity<VietQRDTO> getRequestPaymentQR(
            @RequestParam(value = "amount") String amount) {
        VietQRDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String requestPaymentBankTypeId = EnvironmentUtil.getVNPTEpayRequestPaymentBankTypeId();
            String caiValue = EnvironmentUtil.getVNPTEpayRequestPaymentCAI();
            String bankAccount = EnvironmentUtil.getVNPTEpayRequestPaymentBankAccount();
            String userBankName = EnvironmentUtil.getVNPTEpayRequestPaymentBankUsername();
            String content = "BLUECOM NAP TIEN 247";
            BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(requestPaymentBankTypeId);
            // generate VietQRGenerateDTO
            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
            vietQRGenerateDTO.setCaiValue(caiValue);
            vietQRGenerateDTO.setBankAccount(bankAccount);
            vietQRGenerateDTO.setAmount(amount);
            vietQRGenerateDTO.setContent(content);
            String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
            // generate VietQRDTO
            VietQRDTO vietQRDTO = new VietQRDTO();
            vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
            vietQRDTO.setBankName(bankTypeEntity.getBankName());
            vietQRDTO.setBankAccount(bankAccount);
            vietQRDTO.setUserBankName(userBankName);
            vietQRDTO.setQrCode(qr);
            vietQRDTO.setImgId(bankTypeEntity.getImgId());
            vietQRDTO.setAmount(amount);
            vietQRDTO.setContent(content);
            vietQRDTO.setTransactionId("");
            httpStatus = HttpStatus.OK;
            result = vietQRDTO;
        } catch (Exception e) {
            logger.error("getRequestPaymentQR: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // api nap tien dien thoai
    // 1. check so du
    // 2. call topup
    // 3.1. Thanh cong -> tru tien + insert giao dich
    // 3.2. Khong thanh cong -> bao loi
    @PostMapping("epay/mobile-money")
    public ResponseEntity<ResponseMessageDTO> rechargeMobile(@RequestBody MobileRechargeDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        int paymentVQRType = 0;
        int paymentMobileRechargeType = 1;
        try {
            if (dto != null) {
                // check phone no
                // get carrier Type Id
                // check amount wallet
                // call api topup
                // check response => response and notify
                if (FormatUtil.isNumber(dto.getPhoneNo())) {
                    if (dto.getCarrierTypeId() != null &&
                            !dto.getCarrierTypeId().trim().isEmpty()) {
                        CarrierTypeEntity carrier = carrierTypeService.getCarrierTypeById(dto.getCarrierTypeId());
                        if (carrier != null) {
                            // check payment method
                            if (dto.getPaymentMethod() == null || dto.getPaymentMethod() == 0) {
                                AccountWalletEntity wallet = accountWalletService
                                        .getAccountWalletByUserId(dto.getUserId());
                                if (wallet != null) {
                                    long amount = getAmountByType(dto.getRechargeType());
                                    if (amount != 0) {
                                        long balance = Long.parseLong(wallet.getAmount());
                                        if (balance >= amount) {
                                            // find transaction by otp and userId
                                            String check = transactionWalletService
                                                    .checkExistedTransactionnWallet(dto.getOtp(), dto.getUserId(), 1);
                                            if (check != null && !check.trim().isEmpty()) {
                                                TransactionWalletEntity tranMobileEntity = transactionWalletService
                                                        .getTransactionWalletById(check);
                                                // call api topup
                                                String requestId = VNPTEpayUtil
                                                        .createRequestID(EnvironmentUtil.getVnptEpayPartnerName());
                                                int topupResponseCode = VNPTEpayUtil.topup(requestId,
                                                        EnvironmentUtil.getVnptEpayPartnerName(), carrier.getCode(),
                                                        dto.getPhoneNo(), Integer.parseInt(amount + ""));
                                                // process response
                                                if (topupResponseCode == 0) {
                                                    // success
                                                    /// update transaction wallet status success
                                                    LocalDateTime currentDateTime = LocalDateTime.now();
                                                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                                    transactionWalletService.updateTransactionWallet(1, time,
                                                            amount + "", dto.getPhoneNo(),
                                                            dto.getUserId(), dto.getOtp(), 1);
                                                    //
                                                    ///
                                                    // update wallet
                                                    // update amount account wallet
                                                    AccountWalletEntity accountWalletEntity = accountWalletService
                                                            .getAccountWalletByUserId(dto.getUserId());
                                                    if (accountWalletEntity != null) {
                                                        Long currentAmount = Long
                                                                .parseLong(accountWalletEntity.getAmount());
                                                        Long updatedAmount = currentAmount - amount;
                                                        accountWalletService.updateAmount(updatedAmount + "",
                                                                accountWalletEntity.getId());
                                                        // push notification
                                                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                                        UUID notificationUUID = UUID.randomUUID();
                                                        String notiType = NotificationUtil.getNotiMobileTopup();
                                                        String title = NotificationUtil.getNotiTitleMobileTopup();
                                                        String message = NotificationUtil.getNotiDescMobileTopup1()
                                                                + "+" + nf.format(amount)
                                                                + NotificationUtil.getNotiDescMobileTopup2()
                                                                + dto.getPhoneNo()
                                                                + NotificationUtil.getNotiDescMobileTopup3();
                                                        NotificationEntity notiEntity = new NotificationEntity();
                                                        notiEntity.setId(notificationUUID.toString());
                                                        notiEntity.setRead(false);
                                                        notiEntity.setMessage(message);
                                                        notiEntity.setTime(time);
                                                        notiEntity.setType(
                                                                notiType);
                                                        notiEntity.setUserId(dto.getUserId());
                                                        notiEntity.setData(check);
                                                        notificationService.insertNotification(notiEntity);
                                                        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                                                        fcmTokens = fcmTokenService
                                                                .getFcmTokensByUserId(dto.getUserId());
                                                        Map<String, String> data = new HashMap<>();
                                                        data.put("notificationType",
                                                                notiType);
                                                        data.put("notificationId", notificationUUID.toString());
                                                        data.put("amount", amount + "");
                                                        data.put("transWalletId", check);
                                                        data.put("time", time + "");
                                                        data.put("phoneNo", dto.getPhoneNo());
                                                        data.put("billNumber", tranMobileEntity.getBillNumber());
                                                        data.put("paymentMethod", "0");
                                                        data.put("paymentType", paymentMobileRechargeType + "");
                                                        data.put("status", "SUCCESS");
                                                        data.put("message", "");
                                                        firebaseMessagingService.sendUsersNotificationWithData(data,
                                                                fcmTokens,
                                                                title, message);
                                                        try {
                                                            socketHandler.sendMessageToUser(dto.getUserId(),
                                                                    data);
                                                        } catch (IOException e) {
                                                            logger.error(
                                                                    "WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
                                                                            + e.toString());
                                                        }
                                                        result = new ResponseMessageDTO("SUCCESS", "");
                                                        httpStatus = HttpStatus.OK;
                                                    } else {
                                                        logger.error("rechargeMobile: CANNOT FIND USER INFORMATION");
                                                        result = new ResponseMessageDTO("FAILED", "E59");
                                                        httpStatus = HttpStatus.BAD_REQUEST;
                                                    }

                                                } else if (topupResponseCode == 23 || topupResponseCode == 99) {
                                                    // processing
                                                    // update transaction wallet status vnpt epay pending
                                                    logger.error(
                                                            "rechargeMobile: TRANSACTION FAILED: " + topupResponseCode);
                                                    result = new ResponseMessageDTO("FAILED", "E62");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                } else if (topupResponseCode == 35) {
                                                    // traffic busy
                                                    logger.error(
                                                            "rechargeMobile: VNPT EPAY BUSY TRAFFIC: "
                                                                    + topupResponseCode);
                                                    result = new ResponseMessageDTO("FAILED", "E64");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                } else if (topupResponseCode == 109) {
                                                    // maintain VNPT Epay
                                                    logger.error(
                                                            "rechargeMobile: VNPT EPAY MAINTAN: " + topupResponseCode);
                                                    result = new ResponseMessageDTO("FAILED", "E63");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                } else {
                                                    // failed
                                                    logger.error(
                                                            "rechargeMobile: TRANSACTION FAILED: " + topupResponseCode);
                                                    result = new ResponseMessageDTO("FAILED", "E62");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                }
                                            } else {
                                                logger.error("rechargeMobile: INVALID OTP");
                                                result = new ResponseMessageDTO("FAILED", "E65");
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                            }
                                        } else {
                                            logger.error("rechargeMobile: INVALID ACCOUNT BALANCE");
                                            result = new ResponseMessageDTO("FAILED", "E61");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                        }
                                    } else {
                                        logger.error("rechargeMobile: INVALID RECHARGE TYPE");
                                        result = new ResponseMessageDTO("FAILED", "E60");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    logger.error("rechargeMobile: CANNOT FIND USER INFORMATION");
                                    result = new ResponseMessageDTO("FAILED", "E59");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else if (dto.getPaymentMethod() == 1) {
                                // initial data
                                String check = transactionWalletService
                                        .checkExistedTransactionnWallet(dto.getOtp(), dto.getUserId(), 1);
                                if (check != null && !check.trim().isEmpty()) {
                                    UUID transReceiveUUID = UUID.randomUUID();
                                    UUID transcationReceiveBranchUUID = UUID.randomUUID();
                                    UUID transWalletVQRUUID = UUID.randomUUID();
                                    LocalDateTime currentDateTime = LocalDateTime.now();
                                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                    long amount = getAmountByType(dto.getRechargeType());
                                    //
                                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                                    String billNumberVQR = "VRC" + RandomCodeUtil.generateRandomId(10);
                                    String content = traceId + " " + billNumberVQR;
                                    // get bank recharge information
                                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                                    String bankId = EnvironmentUtil.getBankIdRecharge();
                                    String bankLogo = EnvironmentUtil.getBankLogoIdRecharge();
                                    String cai = EnvironmentUtil.getCAIRecharge();
                                    String businessId = EnvironmentUtil.getBusinessIdRecharge();
                                    String branchId = EnvironmentUtil.getBranchIdRecharge();
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
                                    transactionReceiveEntity.setTime(time);
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
                                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                                    // insert transaction branch
//                                    if (businessId != null && branchId != null && !businessId.trim().isEmpty()
//                                            && !branchId.trim().isEmpty()) {
//                                        TransactionReceiveBranchEntity transactionReceiveBranchEntity = new TransactionReceiveBranchEntity();
//                                        transactionReceiveBranchEntity.setId(transcationReceiveBranchUUID.toString());
//                                        transactionReceiveBranchEntity.setBusinessId(businessId);
//                                        transactionReceiveBranchEntity.setBranchId(branchId);
//                                        transactionReceiveBranchEntity
//                                                .setTransactionReceiveId(transReceiveUUID.toString());
//                                        transactionReceiveBranchService
//                                                .insertTransactionReceiveBranch(transactionReceiveBranchEntity);
//                                    }
                                    // insert transaction_wallet VQR
                                    TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                                    transactionWalletEntity.setId(transWalletVQRUUID.toString());
                                    transactionWalletEntity.setAmount(amount + "");
                                    transactionWalletEntity.setBillNumber(billNumberVQR);
                                    transactionWalletEntity.setContent(content);
                                    transactionWalletEntity.setStatus(0);
                                    transactionWalletEntity.setTimeCreated(time);
                                    transactionWalletEntity.setTimePaid(0);
                                    transactionWalletEntity.setTransType("C");
                                    transactionWalletEntity.setUserId(dto.getUserId());
                                    transactionWalletEntity.setOtp("");
                                    transactionWalletEntity.setPaymentType(paymentVQRType);
                                    transactionWalletEntity.setPaymentMethod(1);
                                    transactionWalletEntity
                                            .setReferenceNumber(paymentMobileRechargeType + "*" + carrier.getCode()
                                                    + "*" + dto.getPhoneNo() + "*" + dto.getUserId() + "*"
                                                    + dto.getOtp());
                                    transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                                    // update transaction_wallet mobile recharge
                                    LocalDateTime currentDateTimeMobile = LocalDateTime.now();
                                    long timeMobile = currentDateTimeMobile.toEpochSecond(ZoneOffset.UTC);
                                    transactionWalletService.updateTransactionWalletConfirm(timeMobile, amount + "",
                                            dto.getUserId(), dto.getOtp(), 1);
                                    // response billNumber*imgId*QR
                                    result = new ResponseMessageDTO("SUCCESS",
                                            billNumberVQR + "*" + bankLogo + "*" + qr);
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

                        } else {
                            logger.error("rechargeMobile: INVALID CARRIER TYPE ID");
                            result = new ResponseMessageDTO("FAILED", "E58");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error("rechargeMobile: INVALID CARRIER TYPE ID");
                        result = new ResponseMessageDTO("FAILED", "E58");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("rechargeMobile: INVALID PHONE NUMBER");
                    result = new ResponseMessageDTO("FAILED", "E57");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("rechargeMobile: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at rechargeMobile: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private long getAmountByType(int type) {
        long result = 0;
        try {
            if (type == 1) {
                result = 10000;
            } else if (type == 2) {
                result = 20000;
            } else if (type == 3) {
                result = 50000;
            } else if (type == 4) {
                result = 100000;
            } else if (type == 5) {
                result = 200000;
            } else if (type == 6) {
                result = 500000;
            } else {
                result = 0;
            }
        } catch (Exception e) {
            result = 0;
        }

        return result;
    }

    // query balance
    @GetMapping("epay/query-balance")
    public ResponseEntity<EpayBalanceDTO> getAccountBalance() {
        EpayBalanceDTO result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("START getPayBalance");
            System.out.println("EnvironmentUtil.getVnptEpayPartnerName(): " + EnvironmentUtil.getVnptEpayPartnerName());
            QueryBalanceResult queryBalanceResult = VNPTEpayUtil.queryBalance(EnvironmentUtil.getVnptEpayPartnerName());
            result = new EpayBalanceDTO(queryBalanceResult.getBalance_money() + "",
                    queryBalanceResult.getBalance_money() + "", queryBalanceResult.getBalance_debit() + "",
                    queryBalanceResult.getBalance_avaiable() + "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at registerAccount: " + e.toString());
            logger.error("Error at registerAccount: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
