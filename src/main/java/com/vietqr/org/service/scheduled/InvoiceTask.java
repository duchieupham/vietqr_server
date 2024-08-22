package com.vietqr.org.service.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceTask {
    private static final Logger logger = Logger.getLogger(InvoiceTask.class);

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceItemService invoiceItemService;

    @Autowired
    private BankReceiveFeePackageService bankReceiveFeePackageService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    FcmTokenService fcmTokenService;

    @Scheduled(fixedRate = 180000) // Chỉnh thành 3 phút
    public void createMonthlyInvoices() {
        List<MerchantSyncEntity> merchants = merchantSyncService.findAllMerchants();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).plusHours(7); // Chuyển sang giờ UTC+7
        LocalDate lastMonth = now.minusMonths(1).toLocalDate();
        String formattedDate = lastMonth.format(DateTimeFormatter.ofPattern("yyyyMM")); // Định dạng lại thành yyyyMM

        for (MerchantSyncEntity merchant : merchants) {
            List<AccountBankReceiveEntity> bankAccountDTOs = accountBankReceiveService.findBankAccountsByMerchantId(merchant.getId());
            for (AccountBankReceiveEntity bankAccountDTO : bankAccountDTOs) {
                createInvoiceForBankAccount(merchant, bankAccountDTO, formattedDate);
            }
        }
    }

    private void createInvoiceForBankAccount(MerchantSyncEntity merchant, AccountBankReceiveEntity bankAccount, String formattedDate) {
        logger.info(String.format("Creating invoice for bank account: %s (Bank ID: %s) of merchant: %s", bankAccount.getBankAccountName(), bankAccount.getId(), merchant.getName()));
        List<InvoiceItemCreateDTO> items = generateInvoiceItems(bankAccount, merchant.getId(), formattedDate);
        if (items.isEmpty()) {
            logger.info(String.format("No invoice items to create for bank account: %s (Bank ID: %s) of merchant: %s", bankAccount.getBankAccountName(), bankAccount.getId(), merchant.getName()));
            return;
        }
        String formattedMonthYear = LocalDate.parse(formattedDate + "01", DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("MM/yyyy"));
        InvoiceCreateUpdateDTO invoiceDTO = new InvoiceCreateUpdateDTO();
        invoiceDTO.setBankId(bankAccount.getId());
        invoiceDTO.setMerchantId(merchant.getId());
        invoiceDTO.setItems(items);
        invoiceDTO.setVat(8.0);
        invoiceDTO.setInvoiceName("Hóa đơn thu phí giao dịch tháng " + formattedMonthYear);
        invoiceDTO.setDescription("Hóa đơn thu phí giao dịch + phí duy trì tháng " + formattedMonthYear);

        // Tạo và lưu hóa đơn
        InvoiceEntity invoiceEntity = calculateInvoice(invoiceDTO, bankAccount.getId(), merchant.getId());
        logger.info(String.format("Invoice created with ID: %s for bank account: %s (Bank ID: %s)", invoiceEntity.getId(), bankAccount.getBankAccountName(), bankAccount.getId()));

        // Gửi thông báo
        sendNotification(invoiceDTO, invoiceEntity, bankAccount.getUserId(), invoiceEntity.getTotalAmount());
    }

    private List<InvoiceItemCreateDTO> generateInvoiceItems(AccountBankReceiveEntity bankAccount, String merchantId, String formattedDate) {
        List<InvoiceItemCreateDTO> items = new ArrayList<>();
        double vat = 8.0;
        // Kiểm tra và thêm phí giao dịch nếu totalAmount > 0
        long totalTransactionAmount = calculateTransactionFee(bankAccount, formattedDate);
        String formattedMonthYear = LocalDate.parse(formattedDate + "01", DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("MM/yyyy"));

        if (totalTransactionAmount > 0) {
            InvoiceItemCreateDTO transactionFeeItem = new InvoiceItemCreateDTO();
            transactionFeeItem.setType(1); // Type cho phí giao dịch
            transactionFeeItem.setTimeProcess(formattedDate);
            transactionFeeItem.setAmount(totalTransactionAmount);
            transactionFeeItem.setContent("Hóa đơn thu phí giao dịch tháng " + formattedMonthYear);
            transactionFeeItem.setQuantity(1);
            transactionFeeItem.setUnit(EnvironmentUtil.getMonthUnitNameVn());
            transactionFeeItem.setTotalAmount(totalTransactionAmount);
            transactionFeeItem.setVat(vat);
            transactionFeeItem.setVatAmount(Math.round(vat / 100 * totalTransactionAmount));
            transactionFeeItem.setAmountAfterVat(Math.round((vat + 100) / 100 * totalTransactionAmount));

            // Kiểm tra nếu mục này đã tồn tại
            if (!isInvoiceItemExist(bankAccount.getId(), merchantId, 1, formattedDate)) {
                items.add(transactionFeeItem);

                // Kiểm tra và thêm phí thường niên nếu có
                long annualFeeAmount = calculateAnnualFee(bankAccount);
                if (annualFeeAmount > 0) {
                    InvoiceItemCreateDTO annualFeeItem = new InvoiceItemCreateDTO();
                    annualFeeItem.setType(0); // Type cho phí thường niên
                    annualFeeItem.setTimeProcess(formattedDate);
                    annualFeeItem.setAmount(annualFeeAmount);
                    annualFeeItem.setContent("Hóa đơn thu phí thường niên " + formattedMonthYear);
                    annualFeeItem.setQuantity(1);
                    annualFeeItem.setVat(vat);
                    annualFeeItem.setUnit(EnvironmentUtil.getMonthUnitNameVn());
                    annualFeeItem.setAmount(annualFeeAmount);
                    annualFeeItem.setTotalAmount(annualFeeAmount);
                    BankReceiveFeePackageEntity feePackage = bankReceiveFeePackageService.getFeePackageByBankIds(bankAccount.getId());
                    if (feePackage != null) {
                        annualFeeItem.setVatAmount(Math.round(feePackage.getVat() / 100 * annualFeeAmount));
                        annualFeeItem.setAmountAfterVat(annualFeeItem.getTotalAmount() + annualFeeItem.getVatAmount());
                    }

                    // Kiểm tra nếu mục này đã tồn tại
                    if (!isInvoiceItemExist(bankAccount.getId(), merchantId, 0, formattedDate)) {
                        items.add(annualFeeItem);
                    }
                }
            }
        }

        logger.info(String.format("Generated %d invoice items for bank account: %s (Bank ID: %s)", items.size(), bankAccount.getBankAccountName(), bankAccount.getId()));
        return items;
    }

    private boolean isInvoiceItemExist(String bankId, String merchantId, int type, String formattedDate) {
        return invoiceItemService.checkInvoiceItemExist(bankId, merchantId, type, formattedDate) > 0;
    }

    private long calculateTransactionFee(AccountBankReceiveEntity bankAccount, String formattedDate) {
        BankReceiveFeePackageEntity feePackage = bankReceiveFeePackageService.getFeePackageByBankIds(bankAccount.getId());
        String year = formattedDate.substring(0, 4);
        String month = formattedDate.substring(4, 6);
        String time = year + "-" + month;
        List<TransReceiveInvoicesDTO> transactionReceiveEntities = transactionReceiveService.getTransactionReceiveByBankId(bankAccount.getId(), time);

        double totalAmountRaw = 0.D;
        if (feePackage != null) {
            int isTotal = feePackage.getRecordType();
            List<TransReceiveInvoicesDTO> transReceiveInvoicesInvoices = new ArrayList<>();
            switch (isTotal) {
                case 0:
                    transReceiveInvoicesInvoices = transactionReceiveEntities.stream().filter(item ->
                            (item.getType() == 0 || item.getType() == 1) &&
                                    "C".equalsIgnoreCase(item.getTransType())
                    ).collect(Collectors.toList());
                    break;
                case 1:
                    transReceiveInvoicesInvoices = transactionReceiveEntities.stream().filter(item ->
                            "C".equalsIgnoreCase(item.getTransType())
                    ).collect(Collectors.toList());
                    break;
            }
            totalAmountRaw = transReceiveInvoicesInvoices.stream()
                    .mapToDouble(item -> {
                        double amount = 0;
                        amount = feePackage.getFixFee()
                                + (feePackage.getPercentFee() / 100)
                                * item.getAmount();
                        return amount;
                    })
                    .sum();
        }
        long annualFee = 0;
        if (feePackage != null) {
            annualFee = feePackage.getAnnualFee();
        }
        long totalAmount = Math.round(totalAmountRaw) - annualFee;
        if (totalAmount < 0) totalAmount = 0;

        logger.info(String.format("Calculated transaction fee for bank account: %s (Bank ID: %s) is %d", bankAccount.getBankAccountName(), bankAccount.getId(), totalAmount));
        return totalAmount;
    }

    private long calculateAnnualFee(AccountBankReceiveEntity bankAccount) {
        BankReceiveFeePackageEntity feePackage = bankReceiveFeePackageService.getFeePackageByBankIds(bankAccount.getId());
        long annualFee = feePackage != null ? feePackage.getAnnualFee() : 0;

        logger.info(String.format("Calculated annual fee for bank account: %s (Bank ID: %s) is %d", bankAccount.getBankAccountName(), bankAccount.getId(), annualFee));
        return annualFee;
    }

    private InvoiceEntity calculateInvoice(InvoiceCreateUpdateDTO dto, String bankId, String merchantId) {
        InvoiceEntity entity = new InvoiceEntity();
        LocalDateTime current = LocalDateTime.now(ZoneOffset.UTC).plusHours(7); // Chuyển sang giờ UTC+7
        UUID invoiceId = UUID.randomUUID();
        String invoiceNumber = "VTS" + RandomCodeUtil.generateOTP(10);

        entity.setId(invoiceId.toString());
        entity.setInvoiceId(invoiceNumber);
        entity.setName(dto.getInvoiceName());
        entity.setDescription(dto.getDescription());
        entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
        entity.setTimePaid(0);
        entity.setStatus(0);
        entity.setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : "");
        entity.setBankId(dto.getBankId() != null ? dto.getBankId() : "");
        entity.setBankIdRecharge(dto.getBankIdRecharge() != null ? dto.getBankIdRecharge() : systemSettingService.getBankIdRechargeDefault());
        entity.setRefId("");
        entity.setFileAttachmentId("");

        String userIdByBankId = accountBankReceiveService.getUserIdByBankId(bankId);
        entity.setUserId(userIdByBankId);

        ObjectMapper mapper = new ObjectMapper();
        try {
            IMerchantBankMapperDTO merchantMapper = null;
            IBankReceiveMapperDTO bankReceiveMapperDTO = null;
            if (StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                bankReceiveMapperDTO = accountBankReceiveService.getMerchantBankMapper(bankId);
            } else {
                merchantMapper = bankReceiveFeePackageService.getMerchantBankMapper(merchantId, bankId);
            }
            MerchantBankMapperDTO merchantBankMapperDTO = new MerchantBankMapperDTO();
            if (merchantMapper != null) {
                AccountBankInfoDTO accountBankInfoDTO = getBankAccountInfoByData(merchantMapper.getData());
                merchantBankMapperDTO.setUserBankName(accountBankInfoDTO.getUserBankName());
                merchantBankMapperDTO.setMerchantName(merchantMapper.getMerchantName());
                merchantBankMapperDTO.setVso(merchantMapper.getVso());
                merchantBankMapperDTO.setEmail(merchantMapper.getEmail());
                merchantBankMapperDTO.setBankAccount(accountBankInfoDTO.getBankAccount());
                merchantBankMapperDTO.setBankShortName(accountBankInfoDTO.getBankShortName());
                merchantBankMapperDTO.setPhoneNo("");
            } else if (bankReceiveMapperDTO != null) {
                merchantBankMapperDTO.setUserBankName(bankReceiveMapperDTO.getUserBankName());
                merchantBankMapperDTO.setMerchantName("");
                merchantBankMapperDTO.setVso(bankReceiveMapperDTO.getVso());
                merchantBankMapperDTO.setEmail(StringUtil.getValueNullChecker(bankReceiveMapperDTO.getEmail()));
                merchantBankMapperDTO.setBankAccount(bankReceiveMapperDTO.getBankAccount());
                merchantBankMapperDTO.setBankShortName(bankReceiveMapperDTO.getBankShortName());
                merchantBankMapperDTO.setPhoneNo(bankReceiveMapperDTO.getPhoneNo());
            }
            entity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
            entity.setDataType(1);
        } catch (Exception ignored) {
            entity.setData("");
            entity.setDataType(9);
        }

        long totalAmount = 0;
        long totalAmountAfterVat = 0;
        long totalVatAmount = 0;

        List<InvoiceItemEntity> invoiceItemEntities = new ArrayList<>();
        for (InvoiceItemCreateDTO item : dto.getItems()) {
            InvoiceItemEntity invoiceItemEntity = new InvoiceItemEntity();
            String invoiceItemId = UUID.randomUUID().toString();
            invoiceItemEntity.setId(invoiceItemId);
            invoiceItemEntity.setInvoiceId(invoiceId.toString());
            invoiceItemEntity.setAmount(item.getAmount());
            invoiceItemEntity.setQuantity(item.getQuantity());
            invoiceItemEntity.setTotalAmount(item.getTotalAmount());
            invoiceItemEntity.setTotalAfterVat(item.getAmountAfterVat());
            invoiceItemEntity.setName(item.getContent());
            invoiceItemEntity.setDescription(item.getContent());

            invoiceItemEntity.setUnit(item.getUnit());
            invoiceItemEntity.setVat(item.getVat());
            invoiceItemEntity.setVatAmount(item.getVatAmount());

            invoiceItemEntity.setData(entity.getData());
            invoiceItemEntity.setDataType(entity.getDataType());
            invoiceItemEntity.setTimeCreated(entity.getTimeCreated());
            invoiceItemEntity.setTimePaid(entity.getTimePaid());
            invoiceItemEntity.setProcessDate(item.getTimeProcess().replaceAll("-", ""));
            invoiceItemEntity.setType(item.getType());
            invoiceItemEntity.setTypeName(item.getType() == 0 ? EnvironmentUtil.getVietQrNameAnnualFee() : EnvironmentUtil.getVietQrNameTransFee());

            totalAmount += item.getTotalAmount();
            totalVatAmount += item.getVatAmount();
            totalAmountAfterVat += item.getAmountAfterVat();

            invoiceItemEntities.add(invoiceItemEntity);
        }

        entity.setTotalAmount(totalAmountAfterVat);
        entity.setAmount(totalAmount);
        entity.setVatAmount(totalVatAmount);
        entity.setVat(dto.getVat());
        invoiceItemService.insertAll(invoiceItemEntities);
        invoiceService.insert(entity);

        logger.info(String.format("Invoice with ID: %s and number: %s has been created.", entity.getId(), entity.getInvoiceId()));
        return entity;
    }

    private AccountBankInfoDTO getBankAccountInfoByData(String data) {
        AccountBankInfoDTO dto = new AccountBankInfoDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            dto = mapper.readValue(data, AccountBankInfoDTO.class);
        } catch (Exception e) {
            dto = new AccountBankInfoDTO();
        }
        return dto;
    }

    private void sendNotification(InvoiceCreateUpdateDTO dto, InvoiceEntity invoiceEntity, String userId, long totalAmountAfterVat) {
        Thread thread2 = new Thread(() -> {
            UUID notificationUUID = UUID.randomUUID();
            String notiType = NotificationUtil.getNotiInvoiceCreated();
            String title = NotificationUtil.getNotiTitleInvoiceUnpaid();
            String message = "Bạn có hoá đơn "
                    + dto.getInvoiceName()
                    + " chưa thanh toán. Vui Lòng kiểm tra lại trên hệ thống VietQR VN.";

            NotificationEntity notiEntity = new NotificationEntity();
            notiEntity.setId(notificationUUID.toString());
            notiEntity.setRead(false);
            notiEntity.setMessage(message);
            notiEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
            notiEntity.setType(notiType);
            notiEntity.setUserId(userId);
            notiEntity.setData(userId);
            Map<String, String> datas = new HashMap<>();
            datas.put("notificationType", notiType);
            datas.put("notificationId", notificationUUID.toString());
            datas.put("status", "1");
            datas.put("bankCode", "MB");
            datas.put("terminalCode", "");
            datas.put("terminalName", "");
            datas.put("html", "<div><span style=\"font-size: 12;\">Bạn có 1 hóa đơn <br><strong> " + StringUtil.formatNumberAsString(totalAmountAfterVat + "") + " VND " +
                    "</strong><br>cần thanh toán!</span></div>");
            datas.put("invoiceId", invoiceEntity.getId());  //invoice ID
            datas.put("time", invoiceEntity.getTimeCreated() + "");
            datas.put("invoiceName", invoiceEntity.getName());
            pushNotification(title, message, notiEntity, datas, notiEntity.getUserId());
        });
        thread2.start();
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId) {
        if (notiEntity != null) {
            notificationService.insertNotification(notiEntity);
        }

        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
        fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
        firebaseMessagingService.sendUsersNotificationWithData(data,
                fcmTokens,
                title, message);
        try {
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
                            + e.getMessage() + System.currentTimeMillis());
        }
    }
}

