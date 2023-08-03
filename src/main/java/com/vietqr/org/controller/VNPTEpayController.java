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
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.EpayBalanceDTO;
import com.vietqr.org.dto.MobileRechargeDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.entity.CarrierTypeEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.service.AccountWalletService;
import com.vietqr.org.service.CarrierTypeService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.service.vnpt.services.QueryBalanceResult;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.FormatUtil;
import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.SocketHandler;
import com.vietqr.org.util.VNPTEpayUtil;

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
    private SocketHandler socketHandler;

    // api nap tien dien thoai
    // 1. check so du
    // 2. call topup
    // 3.1. Thanh cong -> tru tien + insert giao dich
    // 3.2. Khong thanh cong -> bao loi
    @PostMapping("epay/mobile-money")
    public ResponseEntity<ResponseMessageDTO> rechargeMobile(@RequestBody MobileRechargeDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check phone no
            // get carrier Type Id
            // check amount wallet
            // call api topup
            // check response => response and notify
            if (dto != null) {
                if (FormatUtil.isNumber(dto.getPhoneNo())) {
                    if (dto.getCarrierTypeId() != null && !dto.getCarrierTypeId().trim().isEmpty()) {
                        CarrierTypeEntity carrier = carrierTypeService.getCarrierTypeById(dto.getCarrierTypeId());
                        if (carrier != null) {
                            AccountWalletEntity wallet = accountWalletService.getAccountWalletByUserId(dto.getUserId());
                            if (wallet != null) {
                                long amount = getAmountByType(dto.getRechargeType());
                                if (amount != 0) {
                                    long balance = Long.parseLong(wallet.getAmount());
                                    if (balance >= amount) {
                                        // find transaction by otp and userId
                                        String check = transactionWalletService
                                                .checkExistedTransactionnWallet(dto.getOtp(), dto.getUserId(), 1);
                                        if (check != null && !check.trim().isEmpty()) {
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
                                                transactionWalletService.updateTransactionWallet(1, time, amount + "",
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
                                                            + "+" + nf.format(amount + "")
                                                            + NotificationUtil.getNotiDescMobileTopup2()
                                                            + dto.getPhoneNo()
                                                            + NotificationUtil.getNotiDescMobileTopup3();
                                                    NotificationEntity notiEntity = new NotificationEntity();
                                                    notiEntity.setId(notificationUUID.toString());
                                                    notiEntity.setRead(false);
                                                    notiEntity.setMessage(message);
                                                    notiEntity.setTime(time);
                                                    notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                                                    notiEntity.setUserId(dto.getUserId());
                                                    notiEntity.setData(check);
                                                    notificationService.insertNotification(notiEntity);
                                                    List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                                                    fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
                                                    Map<String, String> data = new HashMap<>();
                                                    data.put("notificationType",
                                                            notiType);
                                                    data.put("notificationId", notificationUUID.toString());
                                                    data.put("amount", amount + "");
                                                    data.put("transWalletId", check);
                                                    data.put("time", time + "");
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
                                                        "rechargeMobile: VNPT EPAY BUSY TRAFFIC: " + topupResponseCode);
                                                result = new ResponseMessageDTO("FAILED", "E64");
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                            } else if (topupResponseCode == 109) {
                                                // maintain VNPT Epay
                                                logger.error("rechargeMobile: VNPT EPAY MAINTAN: " + topupResponseCode);
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
            System.out.println("Error at registerAccount: " + e.toString());
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
