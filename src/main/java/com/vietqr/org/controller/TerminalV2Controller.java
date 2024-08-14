package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalV2Controller {
    private static final Logger logger = Logger.getLogger(TerminalV2Controller.class);
    @Autowired
    private TerminalService terminalService;

    @Autowired
    private CustomerInvoiceService customerInvoiceService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private MerchantBankReceiveService merchantBankReceiveService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    @Autowired
    private TerminalBankService terminalBankService;

    @Autowired
    private MerchantMemberService merchantMemberService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private BankTypeService bankTypeService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private SocketHandler socketHandler;

    //sync
    @PostMapping("terminal/v2")
    public ResponseEntity<ResponseMessageDTO> insertTerminal(@Valid @RequestBody TerminalInsertV2DTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if ((dto.getMerchantId() != null && !dto.getMerchantId().trim().isEmpty()) || (dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty())) {
                ObjectMapper mapper = new ObjectMapper();
                if (dto.getBankIds() != null && dto.getBankIds().size() > 1) {
                    result = new ResponseMessageDTO("FAILED", "E111");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.error("TerminalController: insertTerminal: bankIds size > 1");
                } else {
                    UUID uuid = UUID.randomUUID();
                    Map<String, QRStaticCreateDTO> qrMap = new HashMap<>();
                    //return terminal id if the code is existed
                    String checkExistedCode = terminalService.checkExistedTerminal(dto.getCode());
                    if (!StringUtil.isNullOrEmpty(checkExistedCode)) {
                        result = new ResponseMessageDTO("FAILED", "E110");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        TerminalEntity entity = new TerminalEntity();
                        entity.setId(uuid.toString());
                        entity.setName(dto.getName());
                        entity.setCode(dto.getCode());
                        entity.setAddress(StringUtil.isNullOrEmpty(dto.getAddress()) ? "" : dto.getAddress());
                        if (dto.getMerchantId() != null && !dto.getMerchantId().trim().isEmpty()) {
                            // create Terminal with exist Merchant
                            entity.setMerchantId(dto.getMerchantId());
                        } else {
                            // create new Merchant with merchantName
                            UUID _uuid = UUID.randomUUID();
                            MerchantEntity merchantEntity = new MerchantEntity(_uuid.toString(), dto.getMerchantName(), dto.getUserId());
                            merchantService.insertMerchant(merchantEntity);
                            entity.setMerchantId(_uuid.toString());
                        }
                        entity.setUserId(dto.getUserId());
                        entity.setDefault(false);
                        entity.setTimeCreated(time);

                        MerchantBankReceiveEntity merchantBankReceiveEntity = merchantBankReceiveService
                                .getMerchantBankByMerchantId(dto.getMerchantId(), dto.getBankIds().get(0));
                        if (merchantBankReceiveEntity == null) {
                            merchantBankReceiveEntity = new MerchantBankReceiveEntity();
                            merchantBankReceiveEntity.setId(UUID.randomUUID().toString());
                            merchantBankReceiveEntity.setMerchantId(dto.getMerchantId());
                            merchantBankReceiveEntity.setBankId(dto.getBankIds().get(0));
                            merchantBankReceiveService.save(merchantBankReceiveEntity);
                        }
                        terminalService.insertTerminal(entity);

                        // insert account-bank-receive-share
                        List<AccountBankReceiveShareEntity> accountBankReceiveShareEntities = new ArrayList<>();

                        // insert merchant member
                        List<MerchantMemberEntity> entities = new ArrayList<>();
                        List<MerchantMemberRoleEntity> merchantMemberRoleEntities = new ArrayList<>();
                        List<TerminalBankReceiveEntity> terminalBankReceiveEntities = new ArrayList<>();
                        if (!FormatUtil.isListNullOrEmpty(dto.getUserIds())) {
                            for (String userId : dto.getUserIds()) {

                                AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                                accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                                accountBankReceiveShareEntity.setBankId("");
                                accountBankReceiveShareEntity.setUserId(userId);
                                accountBankReceiveShareEntity.setOwner(false);
                                accountBankReceiveShareEntity.setTerminalId(uuid.toString());
                                accountBankReceiveShareEntity.setQrCode("");
                                accountBankReceiveShareEntity.setTraceTransfer("");
                                accountBankReceiveShareEntities.add(accountBankReceiveShareEntity);

                                MerchantMemberEntity merchantMemberEntity = new MerchantMemberEntity();
                                String merchantMemberId = UUID.randomUUID().toString();
                                merchantMemberEntity.setId(merchantMemberId);
                                merchantMemberEntity.setUserId(userId);
                                merchantMemberEntity.setTerminalId(uuid.toString());
                                if (dto.getMerchantId() != null && !dto.getMerchantId().trim().isEmpty()) {
                                    merchantMemberEntity.setMerchantId(dto.getMerchantId());
                                } else {
                                    merchantMemberEntity.setMerchantId("");
                                }
                                merchantMemberEntity.setActive(true);
                                merchantMemberEntity.setTimeAdded(time);

                                List<String> roleReceives = new ArrayList<>();
                                List<String> roleRefunds = new ArrayList<>();
                                roleReceives.add(EnvironmentUtil.getOnlyReadReceiveTerminalRoleId());
                                roleReceives.add(EnvironmentUtil.getFcmNotificationRoleId());
                                MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
                                merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
                                merchantMemberRoleEntity.setMerchantMemberId(merchantMemberId);
                                merchantMemberRoleEntity.setUserId(userId);
                                merchantMemberRoleEntity.setTransReceiveRoleIds(mapper
                                        .writeValueAsString(roleReceives));
                                merchantMemberRoleEntity.setTransRefundRoleIds(mapper
                                        .writeValueAsString(roleRefunds));
                                merchantMemberRoleEntities.add(merchantMemberRoleEntity);
                                entities.add(merchantMemberEntity);
                            }
                        }
                        if (!FormatUtil.isListNullOrEmpty(dto.getBankIds())) {
                            for (String bankId : dto.getBankIds()) {
                                TerminalBankReceiveEntity terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                                terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                                terminalBankReceiveEntity.setBankId(bankId);
                                terminalBankReceiveEntity.setTerminalId(uuid.toString());
                                terminalBankReceiveEntity.setRawTerminalCode("");
                                terminalBankReceiveEntity.setTerminalCode("");
                                terminalBankReceiveEntity.setTypeOfQR(0);

                                AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                                accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                                accountBankReceiveShareEntity.setBankId(bankId);
                                accountBankReceiveShareEntity.setUserId(dto.getUserId());
                                accountBankReceiveShareEntity.setOwner(true);
                                accountBankReceiveShareEntity.setTerminalId(uuid.toString());


                                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(bankId);
                                if (Objects.nonNull(accountBankReceiveEntity)) {
                                    BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankReceiveEntity.getBankTypeId());
                                    switch (bankTypeEntity.getBankCode()) {
                                        case "MB":
                                            if (accountBankReceiveEntity != null) {
                                                // luồng ưu tiên
                                                if (accountBankReceiveEntity.isMmsActive()) {
                                                    TerminalBankEntity terminalBankEntity =
                                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                                    if (terminalBankEntity != null) {
                                                        String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                                                        terminalBankEntity.getTerminalId(), dto.getCode()));
                                                        terminalBankReceiveEntity.setData1("");
                                                        terminalBankReceiveEntity.setData2(qr);
                                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                                        terminalBankReceiveEntity.setTraceTransfer(traceTransfer);

                                                        accountBankReceiveShareEntity.setQrCode(qr);
                                                        accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
                                                        qrMap.put(bankId, new QRStaticCreateDTO(qr, traceTransfer));


                                                    } else {
                                                        logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                                    }
                                                } else {
                                                    // luồng thuong
                                                    String qrCodeContent = "SQR" + dto.getCode();
                                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
                                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(bankId);
                                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                                    terminalBankReceiveEntity.setData1(qr);
                                                    terminalBankReceiveEntity.setData2("");
                                                    terminalBankReceiveEntity.setTraceTransfer("");

                                                    accountBankReceiveShareEntity.setQrCode(qr);
                                                    accountBankReceiveShareEntity.setTraceTransfer("");
                                                    qrMap.put(bankId, new QRStaticCreateDTO(qr, ""));

                                                }
                                            }
                                            accountBankReceiveShareEntities.add(accountBankReceiveShareEntity);
                                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                                            break;
                                        case "BIDV":
                                            String qr = "";
                                            String billId = RandomCodeUtil.getRandomBillId();
                                            VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                            vietQRCreateDTO.setBankId(accountBankReceiveEntity.getId());
                                            vietQRCreateDTO.setAmount("0");
                                            vietQRCreateDTO.setContent(billId);
                                            vietQRCreateDTO.setUserId(accountBankReceiveEntity.getUserId());
                                            vietQRCreateDTO.setTerminalCode(dto.getCode());

                                            ResponseMessageDTO responseMessageDTO =
                                                    insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankReceiveEntity, billId);
                                            if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                                VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                                vietQRVaRequestDTO.setAmount("0");
                                                vietQRVaRequestDTO.setBillId(billId);
                                                vietQRVaRequestDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
                                                vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));
                                                ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil
                                                        .generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankReceiveEntity.getCustomerId());
                                                if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                                    qr = generateVaInvoiceVietQR.getMessage();
                                                    terminalBankReceiveEntity.setData1(qr);
                                                    terminalBankReceiveEntity.setData2("");
                                                    terminalBankReceiveEntity.setTraceTransfer("");

                                                    accountBankReceiveShareEntity.setQrCode(qr);
                                                    accountBankReceiveShareEntity.setTraceTransfer("");
                                                    qrMap.put(bankId, new QRStaticCreateDTO(qr, ""));
                                                }
                                            }
                                            accountBankReceiveShareEntities.add(accountBankReceiveShareEntity);
                                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                                            break;
                                    }
                                }
                            }
                        }

                        if (!FormatUtil.isListNullOrEmpty(dto.getUserIds())
                                && !FormatUtil.isListNullOrEmpty(dto.getBankIds())) {
                            for (String userId : dto.getUserIds()) {
                                for (String bankId : dto.getBankIds()) {
                                    AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                                    accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                                    accountBankReceiveShareEntity.setBankId(bankId);
                                    accountBankReceiveShareEntity.setUserId(userId);
                                    accountBankReceiveShareEntity.setOwner(false);
                                    QRStaticCreateDTO qrStaticCreateDTO = qrMap.get(bankId);
                                    if (qrStaticCreateDTO != null) {
                                        accountBankReceiveShareEntity.setTraceTransfer(qrStaticCreateDTO.getTraceTransfer());
                                        accountBankReceiveShareEntity.setQrCode(qrStaticCreateDTO.getQrCode());
                                    }
                                    accountBankReceiveShareEntity.setTerminalId(uuid.toString());
                                    accountBankReceiveShareEntities.add(accountBankReceiveShareEntity);
                                }
                            }
                        }

                        accountBankReceiveShareService.insertAccountBankReceiveShare(accountBankReceiveShareEntities);
                        merchantMemberRoleService.insertAll(merchantMemberRoleEntities);
                        terminalBankReceiveService.insertAll(terminalBankReceiveEntities);
                        merchantMemberService.insertAll(entities);
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    }
                }
            } else {
                logger.error("TerminalController: insertTerminal: merchantEntity is null");
                result = new ResponseMessageDTO("FAILED", "E181");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            if (httpStatus.is2xxSuccessful()) {
                Thread thread = new Thread(() -> {
                    if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
                        int numThread = dto.getUserIds().size();
                        ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                        for (String userId : dto.getUserIds()) {
                            Map<String, String> data = new HashMap<>();
                            // insert notification
                            UUID notificationUUID = UUID.randomUUID();
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                            String title = NotificationUtil.getNotiTitleAddMember();
                            String message = String.format(NotificationUtil.getNotiDescAddMember(), dto.getName());
                            NotificationEntity notiEntity = new NotificationEntity();
                            notiEntity.setId(notificationUUID.toString());
                            notiEntity.setRead(false);
                            notiEntity.setMessage(message);
                            notiEntity.setTime(time);
                            notiEntity.setType(NotificationUtil.getNotiTypeAddMember());
                            notiEntity.setUserId(userId);
                            notiEntity.setData(dto.getCode());
                            // data thay đổi
                            data.put("notificationType", NotificationUtil.getNotiTypeAddMember());
                            data.put("notificationId", notificationUUID.toString());
                            data.put("terminalCode", dto.getCode());
                            data.put("terminalName", dto.getName());
                            executorService.submit(() -> pushNotification(title, message, notiEntity, data, userId));
                        }
                        executorService.shutdown();
                    }
                });
                thread.start();
            }
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
                                                                 AccountBankReceiveEntity accountBankEntity, String billId) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            long amount = 0;
            if (Objects.nonNull(accountBankEntity) && !StringUtil.isNullOrEmpty(billId)) {
                if (!StringUtil.isNullOrEmpty(accountBankEntity.getCustomerId())) {
                    CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setCustomerId(accountBankEntity.getCustomerId());
                    try {
                        amount = Long.parseLong(dto.getAmount());
                    } catch (Exception e) {
                        logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
                    }
                    entity.setAmount(amount);
                    entity.setBillId(billId);
                    entity.setStatus(0);
                    entity.setType(1);
                    entity.setName(dto.getTerminalCode());
                    entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setTimePaid(0L);
                    entity.setInquire(0);
                    entity.setQrType(2);
                    customerInvoiceService.insert(entity);
                    responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    responseMessageDTO = new ResponseMessageDTO("FAILED", "");
                }
            } else {
                responseMessageDTO = new ResponseMessageDTO("FAILED", "");
            }
        } catch (Exception e) {
            logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
        return responseMessageDTO;
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId) {
        try {
            if (notiEntity != null) {
                notificationService.insertNotification(notiEntity);
            }
            List<FcmTokenEntity> fcmTokens = new ArrayList<>();
            fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
            firebaseMessagingService.sendUsersNotificationWithData(data,
                    fcmTokens,
                    title, message);
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "Add member to terminal: WS: push Notification - RECHARGE ERROR: "
                            + e.toString());
        }
    }
}
