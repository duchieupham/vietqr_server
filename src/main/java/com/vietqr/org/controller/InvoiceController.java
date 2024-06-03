package com.vietqr.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
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
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class InvoiceController {

    private static final Logger logger = Logger.getLogger(InvoiceController.class);

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    BankReceiveFeePackageService bankReceiveFeePackageService;

    @Autowired
    InvoiceTransactionService invoiceTransactionService;

    @Autowired
    ProcessedInvoiceService processedInvoiceService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionWalletService transactionWalletService;

    @Autowired
    BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private SocketHandler socketHandler;

    @GetMapping("invoice/merchant-list")
    public ResponseEntity<Object> getAdminInvoiceLists(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO response = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<IMerchantInvoiceDTO> dtos = merchantSyncService.getMerchantSyncsByName(value, offset, size);
            totalElement = merchantSyncService.countMerchantSyncsByName(value);
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPage(page);
            pageDTO.setSize(size);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageDTO.setTotalElement(totalElement);
            response.setMetadata(pageDTO);
            List<MerchantInvoiceDTO> data = dtos.stream().map(item -> {
                MerchantInvoiceDTO dto = new MerchantInvoiceDTO();
                dto.setMerchantId(item.getMerchantId());
                dto.setMerchantName(item.getMerchantName());
                dto.setPlatform(item.getPlatform());
                dto.setVsoCode(item.getVsoCode());
                dto.setNumberOfBank(StringUtil.getValueNullChecker(item.getNumberOfBank()));
                return dto;
            }).collect(Collectors.toList());
            response.setData(data);
            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getAdminInvoiceLists: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice/update-bank-recharge")
    public ResponseEntity<Object> updateBankRecharge(
            @Valid @RequestBody UpdateBankDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            systemSettingService.updateBankRecharge(dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: updateBankRecharge: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice/bank-account-list")
    public ResponseEntity<Object> getBankAccountList(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String merchantId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<BankAccountInvoiceDTO> data = new ArrayList<>();
            if (StringUtil.isNullOrEmpty(merchantId)) {
                List<IBankAccountInvoiceInfoDTO> infos = new ArrayList<>();
                infos = accountBankReceiveService
                        .getBankInvoiceByBankAccount(value, offset, size);
                Set<String> bankTypeIds = infos.stream()
                        .map(IBankAccountInvoiceInfoDTO::getBankTypeId).collect(Collectors.toSet());
                List<BankTypeShortNameDTO> bankTypeShortNameDTOS = bankTypeService
                        .getBankTypeByListId(new ArrayList<>(bankTypeIds));
                Map<String, String> bankShortNameMap = bankTypeShortNameDTOS.stream()
                        .filter(dto -> dto.getBankTypeId() != null && dto.getBankShortName() != null)
                        .collect(Collectors.toMap(
                                BankTypeShortNameDTO::getBankTypeId,
                                BankTypeShortNameDTO::getBankShortName
                        ));
                data = infos.stream().map(item -> {
                    BankAccountInvoiceDTO dto = new BankAccountInvoiceDTO();
                    dto.setBankId(item.getBankId());
                    dto.setMerchantId(StringUtil.getValueNullChecker(item.getMerchantId()));
                    dto.setUserBankName(item.getUserBankName());
                    dto.setBankShortName(bankShortNameMap.getOrDefault(item.getBankTypeId(), ""));
                    dto.setPhoneNo(item.getPhoneNo());
                    dto.setEmail(StringUtil.getValueNullChecker(item.getEmail()));
                    dto.setBankAccount(item.getBankAccount());
                    dto.setFeePackage(StringUtil.getValueNullChecker(item.getFeePackage()));
                    if (item.getMmsActive() != null && item.getMmsActive()) {
                        dto.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                    } else {
                        dto.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                    }
                    return dto;
                }).collect(Collectors.toList());
                totalElement = accountBankReceiveService
                        .countBankInvoiceByBankAccount(value);
            } else {
                List<IBankAccountInvoiceDTO> dtos = new ArrayList<>();
                dtos = bankReceiveFeePackageService
                        .getBankInvoiceByBankAccountAndMerchantId(merchantId, value, offset, size);
                totalElement = bankReceiveFeePackageService
                        .countBankInvoiceByBankAccountAndMerchantId(merchantId, value);
                data = dtos.stream().map(item -> {
                    BankAccountInvoiceDTO dto = new BankAccountInvoiceDTO();
                    AccountBankInfoDTO bankAccountInfoDTO = getBankAccountInfoByData(item.getData());
                    dto.setBankId(item.getBankId());
                    dto.setMerchantId(StringUtil.getValueNullChecker(merchantId));
                    dto.setUserBankName(bankAccountInfoDTO.getUserBankName());
                    dto.setBankShortName(bankAccountInfoDTO.getBankShortName());
                    dto.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    dto.setEmail(StringUtil.getValueNullChecker(item.getEmail()));
                    dto.setPhoneNo(item.getPhoneNo());
                    dto.setFeePackage(StringUtil.getValueNullChecker(item.getFeePackage()));
                    if (bankAccountInfoDTO.getMmsActive() != null && bankAccountInfoDTO.getMmsActive()) {
                        dto.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                    } else {
                        dto.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                    }
                    return dto;
                }).collect(Collectors.toList());
            }
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
            logger.error("InvoiceController: ERROR: getBankAccountList: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice/admin-list")
    public ResponseEntity<Object> getInvoiceListAdmin(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String time
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        AdminExtraInvoiceDTO extraInvoiceDTO = new AdminExtraInvoiceDTO();
        DataDTO dataDTO = new DataDTO(extraInvoiceDTO);
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<AdminInvoiceDTO> data = new ArrayList<>();
            List<IAdminInvoiceDTO> dtos = new ArrayList<>();
            IAdminExtraInvoiceDTO extraInvoiceDTO1 = null;
            switch (type) {
                case 0:
                    dtos = invoiceService.getInvoiceByMerchantId(value, offset, size, time);
                    totalElement = invoiceService.countInvoiceByMerchantId(value, time);
                    break;
                case 1:
                    dtos = invoiceService.getInvoiceByInvoiceNumber(value, offset, size, time);
                    totalElement = invoiceService.countInvoiceByInvoiceNumber(value, time);
                    break;
                case 2:
                    dtos = invoiceService.getInvoiceByBankAccount(value, offset, size, time);
                    totalElement = invoiceService.countInvoiceByBankAccount(value, time);
                    break;
                case 3:
                    dtos = invoiceService.getInvoiceByPhoneNo(value, offset, size, time);
                    totalElement = invoiceService.countInvoiceByPhoneNo(value, time);
                    break;
                case 4:
                    int status = 0;
                    try {
                        status = Integer.parseInt(value);
                    } catch (Exception ignored) {
                    }
                    dtos = invoiceService.getInvoiceByStatus(status, offset, size, time);
                    totalElement = invoiceService.countInvoiceByStatus(status, time);
                    break;
                case 9:
                    dtos = invoiceService.getInvoices(offset, size, time);
                    totalElement = invoiceService.countInvoice(time);
                    break;
                default:
                    dtos = new ArrayList<>();
                    break;
            }
            extraInvoiceDTO1 = invoiceItemService.getExtraInvoice(time);
            if (extraInvoiceDTO1 != null) {
                extraInvoiceDTO = new AdminExtraInvoiceDTO();
                extraInvoiceDTO.setMonth(time);
                extraInvoiceDTO.setCompleteCount(extraInvoiceDTO1.getCompleteCount());
                extraInvoiceDTO.setCompleteAmount(extraInvoiceDTO1.getCompleteFee());
                extraInvoiceDTO.setPendingCount(extraInvoiceDTO1.getPendingCount());
                extraInvoiceDTO.setPendingAmount(extraInvoiceDTO1.getPendingFee());
                extraInvoiceDTO.setUnFullyPaidCount(extraInvoiceDTO1.getUnfullyPaidCount());
            }
            data = dtos.stream().map(item -> {
                AdminInvoiceDTO dto = new AdminInvoiceDTO();
                AccountBankInfoDTO bankInfoDTO = getBankAccountInfoByData(item.getData());
                String qrCode = "";
                dto.setInvoiceId(item.getInvoiceId());
                dto.setTimePaid(item.getTimePaid());
                dto.setVso(item.getVso() != null ? item.getVso() : "");
                dto.setMidName(item.getMidName() != null ? item.getMidName() : "");
                dto.setAmount(item.getAmount());
                dto.setBankShortName(bankInfoDTO.getBankShortName());
                dto.setBankAccount(bankInfoDTO.getBankAccount());
                dto.setQrCode(qrCode);
                dto.setVat(item.getVat());
                dto.setVatAmount(item.getVatAmount());
                dto.setAmountNoVat(item.getAmountNoVat());
                dto.setBillNumber(item.getBillNumber());
                dto.setInvoiceName(item.getInvoiceName());
                dto.setFullName(bankInfoDTO.getUserBankName());
                dto.setPhoneNo(item.getPhoneNo());
                dto.setEmail(item.getEmail() != null ? item.getEmail() : "");
                dto.setTimeCreated(item.getTimeCreated());
                dto.setStatus(item.getStatus());
                return dto;
            }).collect(Collectors.toList());
            PageDTO pageDTO = new PageDTO();
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageDTO.setTotalElement(totalElement);
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageResponseDTO.setMetadata(pageDTO);
            dataDTO.setExtraData(extraInvoiceDTO);
            dataDTO.setItems(data);
            pageResponseDTO.setData(dataDTO);
            httpStatus = HttpStatus.OK;
            result = pageResponseDTO;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceListAdmin: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("admin/bank-detail")
    public ResponseEntity<Object> getBankAccountDetail(
            @RequestParam String bankId,
            @RequestParam String merchantId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            IBankDetailAdminDTO dto
                    = bankReceiveFeePackageService.getBankReceiveByBankId(bankId);
            if (dto != null) {
                BankDetailAdminDTO data = new BankDetailAdminDTO();
                data.setBankId(dto.getBankId());
                data.setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : "");
                AccountBankInfoDTO bankInfoDTO = getBankAccountInfoByData(dto.getData());
                data.setBankAccount(bankInfoDTO.getBankAccount());
                data.setBankShortName(bankInfoDTO.getBankShortName());
                data.setPhoneNo(dto.getPhoneNo());
                data.setUserBankName(bankInfoDTO.getUserBankName());
                data.setEmail(StringUtil.getValueNullChecker(dto.getEmail()));
                if (bankInfoDTO.getMmsActive()) {
                    data.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                } else {
                    data.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                }
                data.setFeePackage(dto.getFeePackage());
                data.setVat(dto.getVat());
                data.setTransFee1(dto.getTransFee1());
                data.setTransFee2(dto.getTransFee2());
                data.setTransRecord(dto.getTransRecord());
                httpStatus = HttpStatus.OK;
                result = data;
            } else {
                Double vat = systemSettingService.getVatSystemSetting();
                AccountBankDetailAdminDTO detailAdmin = accountBankReceiveService.getAccountBankDetailAdmin(bankId);
                if (detailAdmin != null) {
                    BankDetailAdminDTO data = new BankDetailAdminDTO();
                    data.setBankId(detailAdmin.getBankId());
                    data.setMerchantId("");
                    data.setBankAccount(detailAdmin.getBankAccount());
                    data.setBankShortName(detailAdmin.getBankShortName());
                    data.setPhoneNo(detailAdmin.getPhoneNo());
                    data.setUserBankName(detailAdmin.getUserBankName());
                    data.setEmail(StringUtil.getValueNullChecker(detailAdmin.getEmail()));
                    if (detailAdmin.getMmsActive()) {
                        data.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                    } else {
                        data.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                    }
                    data.setFeePackage("");
                    data.setVat(vat);
                    data.setTransFee1(0);
                    data.setTransFee2(0);
                    data.setTransRecord(0);
                    httpStatus = HttpStatus.OK;
                    result = data;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getBankAccountDetail: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice/qr-detail/{invoiceId}")
    public ResponseEntity<Object> getDetailQrCode(
            @PathVariable String invoiceId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceQrDetailDTO data = new InvoiceQrDetailDTO();
            IInvoiceQrDetailDTO dto = invoiceService.getInvoiceQrById(invoiceId);
            if (dto != null) {
                if (!StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                    IMerchantInfoDTO merchantInfoDTO = merchantSyncService.getMerchantSyncInfo(dto.getMerchantId());
                    if (merchantInfoDTO != null) {
                        data.setVso(StringUtil.getValueNullChecker(merchantInfoDTO.getVso()));
                        data.setMidName(StringUtil.getValueNullChecker(merchantInfoDTO.getMidName()));
                    }
                }
                data.setQrCode(generateQrForPayment(dto.getTotalAmountAfterVat(), dto.getContent()));
                data.setTotalAmountAfterVat(dto.getTotalAmountAfterVat());
                data.setInvoiceName(dto.getInvoiceName());
                data.setBankAccount(StringUtil.removeMarkString(dto.getBankAccount()));
                data.setBankShortName(StringUtil.removeMarkString(dto.getBankShortName()));
                data.setInvoiceNumber(dto.getInvoiceNumber());
                data.setUserBankName(StringUtil.removeMarkString(dto.getUserBankName()));
                data.setTotalAmount(dto.getTotalAmount());
                data.setVat(dto.getVat());
                data.setVatAmount(dto.getVatAmount());
                data.setInvoiceId(dto.getInvoiceId());
            } else {
                data.setInvoiceId(invoiceId);
            }
            result = data;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getDetailQrCode: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice/update/{invoiceId}")
    public ResponseEntity<Object> updateInvoiceItem(
            @PathVariable String invoiceId,
            @Valid @RequestBody InvoiceCreateUpdateDTO dto

    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long totalAmount = 0;
            long totalAmountAfterVat = 0;
            long totalVatAmount = 0;
            ObjectMapper mapper = new ObjectMapper();
            InvoiceEntity entity = invoiceService.getInvoiceEntityById(invoiceId);
            entity.setId(invoiceId);
            entity.setName(dto.getInvoiceName());
            entity.setDescription(dto.getDescription());
            entity.setTimePaid(0);
            entity.setStatus(0);
            entity.setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : "");
            entity.setBankId(dto.getBankId() != null ? dto.getBankId() : "");
            IMerchantBankMapperDTO merchantMapper = null;
            IBankReceiveMapperDTO bankReceiveMapperDTO = null;
            if (StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                bankReceiveMapperDTO = accountBankReceiveService
                        .getMerchantBankMapper(dto.getBankId());
            } else {
                merchantMapper = bankReceiveFeePackageService
                        .getMerchantBankMapper(dto.getMerchantId(), dto.getBankId());
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
                merchantBankMapperDTO.setPhoneNo(merchantBankMapperDTO.getPhoneNo());

                entity.setUserId(merchantMapper.getUserId());
            } else if (bankReceiveMapperDTO != null) {
                merchantBankMapperDTO.setUserBankName(bankReceiveMapperDTO.getUserBankName());
                merchantBankMapperDTO.setMerchantName("");
                merchantBankMapperDTO.setVso("");
                merchantBankMapperDTO.setEmail(bankReceiveMapperDTO.getEmail());
                merchantBankMapperDTO.setBankAccount(bankReceiveMapperDTO.getBankAccount());
                merchantBankMapperDTO.setBankShortName(bankReceiveMapperDTO.getBankShortName());
                merchantBankMapperDTO.setPhoneNo(bankReceiveMapperDTO.getPhoneNo());
                entity.setUserId(bankReceiveMapperDTO.getUserId());
            } else {
                entity.setUserId("");
            }
            try {
                entity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
                entity.setDataType(1);
            } catch (Exception ignored) {
                entity.setData("");
                entity.setDataType(9);
            }
            List<InvoiceItemEntity> invoiceItemEntities = new ArrayList<>();
            List<String> itemIds = new ArrayList<>();
            for (InvoiceItemCreateDTO item : dto.getItems()) {

                InvoiceItemEntity invoiceItemEntity
                        = invoiceItemService.getInvoiceItemById(item.getItemId());
                if (invoiceItemEntity == null) {
                    continue;
                }
                itemIds.add(item.getItemId());
                invoiceItemEntity.setId(item.getItemId());
                invoiceItemEntity.setInvoiceId(invoiceId);
                invoiceItemEntity.setAmount(item.getAmount());
                invoiceItemEntity.setQuantity(item.getQuantity());
                invoiceItemEntity.setTotalAmount(item.getAmount());
                invoiceItemEntity.setTotalAfterVat(item.getAmountAfterVat());
                invoiceItemEntity.setName(item.getContent());
                invoiceItemEntity.setDescription(item.getContent());
                switch (item.getType()) {
                    case 0:
                        invoiceItemEntity.setType(0);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameAnnualFee());
                        break;
                    case 1:
                        invoiceItemEntity.setType(1);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameAnnualFee());
                        break;
                    case 9:
                        invoiceItemEntity.setType(9);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameAnotherFee());
                        break;
                }
                invoiceItemEntity.setUnit(item.getUnit());
                invoiceItemEntity.setVat(item.getVat());
                invoiceItemEntity.setVatAmount(item.getVatAmount());
                invoiceItemEntity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
                invoiceItemEntity.setDataType(1);
                invoiceItemEntities.add(invoiceItemEntity);
                totalAmount += item.getTotalAmount();
                totalVatAmount += item.getVatAmount();
                totalAmountAfterVat += item.getAmountAfterVat();
            }
            entity.setTotalAmount(totalAmountAfterVat);
            entity.setAmount(totalAmount);
            entity.setVatAmount(totalVatAmount);

            String userId = accountBankReceiveService.getUserIdByBankId(dto.getBankId());
            if (userId != null && !userId.isEmpty()) {
                entity.setUserId(userId);
            } else {
                entity.setUserId("");
            }

            InvoiceUpdateItemDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
            //update transaction wallet and transaction_receive
            // update transaction_wallet_credit and debit
            String refIdDebit = transactionWalletService.getRefIdDebitByInvoiceRefId(invoiceDTO.getRefId());
            TransWalletUpdateDTO transWalletUpdateDTO
                    = transactionWalletService.getBillNumberByRefIdTransWallet(refIdDebit);
            String bankId = EnvironmentUtil.getBankIdRecharge();
            TransactionReceiveUpdateDTO transactionReceiveUpdateDTO
                    = transactionReceiveService
                    .getTransactionUpdateByBillNumber(transWalletUpdateDTO.getBillNumber(), bankId, transWalletUpdateDTO.getTimeCreated());
            // update
            transactionWalletService.updateAmountTransWallet(invoiceDTO.getRefId(), totalAmountAfterVat + "");
            transactionWalletService.updateAmountTransWallet(refIdDebit, totalAmountAfterVat + "");
            // update transaction_receive
            // update qr
            // generate VQR
            String bankAccount = EnvironmentUtil.getBankAccountRecharge();
            String cai = EnvironmentUtil.getCAIRecharge();
            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
            vietQRGenerateDTO.setCaiValue(cai);
            vietQRGenerateDTO.setBankAccount(bankAccount);
            vietQRGenerateDTO.setAmount(totalAmountAfterVat + "");
            vietQRGenerateDTO.setContent(transactionReceiveUpdateDTO.getContent());
            String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
            transactionReceiveService
                    .updateTransactionReceiveForInvoice(totalAmountAfterVat, qr, transactionReceiveUpdateDTO.getId());

            invoiceItemService.removeByInvoiceIdInorge(invoiceId, itemIds);
            invoiceItemService.insertAll(invoiceItemEntities);
            invoiceService.insert(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: updateInvoiceItem: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice/edit-detail/{invoiceId}")
    public ResponseEntity<Object> getInvoiceForEdit(
            @PathVariable String invoiceId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceEditDetailDTO data = new InvoiceEditDetailDTO();
            IInvoiceQrDetailDTO dto = invoiceService.getInvoiceQrById(invoiceId);
            if (dto != null) {
                data.setInvoiceId(dto.getInvoiceId());
                data.setInvoiceName(dto.getInvoiceName());
                data.setDescription(dto.getDescription());
                data.setTotalAmount(dto.getTotalAmount());
                data.setVatAmount(dto.getVatAmount());
                data.setTotalAfterVat(dto.getTotalAmountAfterVat());
                InvoiceDetailCustomerDTO customerDTO = new InvoiceDetailCustomerDTO();
                if (dto.getMerchantId() != null) {
                    IMerchantEditDetailDTO merchantEditDetail
                            = merchantSyncService.getMerchantEditDetail(dto.getMerchantId());
                    if (merchantEditDetail != null) {
                        customerDTO.setMerchantId(merchantEditDetail.getMerchantId());
                        customerDTO.setMerchantName(merchantEditDetail.getMerchantName());
                    } else {
                        customerDTO.setMerchantId("");
                        customerDTO.setMerchantName("");
                    }
                } else {
                    customerDTO.setMerchantId("");
                    customerDTO.setMerchantName("");
                }

                if (dto.getBankId() != null && !StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                    IBankAccountInvoiceDTO bankAccountInvoiceDTO =
                            bankReceiveFeePackageService
                                    .getBankInvoiceByBankId(dto.getBankId());
                    if (bankAccountInvoiceDTO != null) {
                        customerDTO.setBankId(bankAccountInvoiceDTO.getBankId());
                        customerDTO.setPhoneNo(bankAccountInvoiceDTO.getPhoneNo());
                        customerDTO.setEmail(bankAccountInvoiceDTO.getEmail());
                        customerDTO.setFeePackage(bankAccountInvoiceDTO.getFeePackage());
                        AccountBankInfoDTO bankAccountInfoDTO = getBankAccountInfoByData(bankAccountInvoiceDTO.getData());
                        customerDTO.setBankAccount(bankAccountInfoDTO.getBankAccount());
                        customerDTO.setBankShortName(bankAccountInfoDTO.getBankShortName());
                        customerDTO.setUserBankName(bankAccountInfoDTO.getUserBankName());
                        if (bankAccountInfoDTO.getMmsActive()) {
                            customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                        } else {
                            customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                        }
                        customerDTO.setVat(dto.getVat());
                        data.setVat(dto.getVat());
                    } else {
                        double vat = systemSettingService.getVatSystemSetting();
                        data.setVat(vat);
                    }
                } else {
                    IBankAccountInvoicesDTO dto1 = accountBankReceiveService.getBankAccountInvoices(dto.getBankId());
                    if (dto1 != null) {
                        customerDTO.setBankId(dto1.getBankId());
                        customerDTO.setPhoneNo(dto1.getPhoneNo());
                        customerDTO.setEmail(dto1.getEmail());
                        customerDTO.setUserBankName(dto1.getUserBankName());
                        customerDTO.setBankShortName(dto1.getBankShortName());
                        customerDTO.setBankAccount(dto1.getBankAccount());
                        if (dto1.getMmsActive()) {
                            customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                        } else {
                            customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                        }
                        customerDTO.setFeePackage("");
                        customerDTO.setVat(dto.getVat());
                        data.setVat(dto.getVat());
                    }

                }
                data.setUserInformation(customerDTO);

                List<IInvoiceItemDetailDTO> invoiceItemDetailDTOS = invoiceItemService.getInvoiceItemsByInvoiceId(invoiceId);
                List<InvoiceItemDetailDTO> invoiceItems
                        = invoiceItemDetailDTOS.stream().map(item -> {
                    InvoiceItemDetailDTO detailDTO = new InvoiceItemDetailDTO();
                    detailDTO.setInvoiceItemId(item.getInvoiceItemId());
                    detailDTO.setInvoiceItemName(item.getInvoiceItemName());
                    detailDTO.setUnit(item.getUnit());
                    detailDTO.setType(item.getType());
                    detailDTO.setQuantity(item.getQuantity());
                    detailDTO.setAmount(item.getAmount());
                    detailDTO.setTotalAmount(item.getTotalAmount());
                    detailDTO.setVat(item.getVat());
                    detailDTO.setVatAmount(item.getVatAmount());
                    detailDTO.setTotalAmountAfterVat(item.getAmountAfterVat());
                    return detailDTO;
                }).collect(Collectors.toList());
                data.setInvoiceItems(invoiceItems);
            } else {
                data.setInvoiceId(invoiceId);
            }
            result = data;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceForEdit: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice/request-payment")
    public ResponseEntity<Object> requestPayment(
            @Valid @RequestBody PaymentRequestDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            PaymentRequestResponseDTO responseDTO = new PaymentRequestResponseDTO();
            String bankIdRechargeDefault = invoiceService.getBankIdRechargeDefault(dto.getInvoiceId());
            String bankId = StringUtil.getValueNullChecker(bankIdRechargeDefault);
            if (!StringUtil.isNullOrEmpty(dto.getBankIdRecharge())) {
                bankId = dto.getBankIdRecharge();
            }
            ObjectMapper mapper = new ObjectMapper();
            String itemIds = getSortedListString(dto.getItemItemIds());
            InvoiceRequestPaymentDTO requestPaymentDTO = invoiceTransactionService.getInvoiceRequestPayment(
                    dto.getInvoiceId(),
                    itemIds,
                    bankId
            );
            if (requestPaymentDTO != null) {
                MerchantBankMapperDTO merchantBankMapperDTO = null;
                merchantBankMapperDTO = getMerchantBankMapperDTO(requestPaymentDTO.getData());
                responseDTO.setQrCode(requestPaymentDTO.getQrCode());
                responseDTO.setTotalAmountAfterVat(requestPaymentDTO.getTotalAMount());
                responseDTO.setInvoiceName(requestPaymentDTO.getInvoiceName());
                responseDTO.setMidName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getMerchantName()));
                responseDTO.setVso(StringUtil.getValueNullChecker(merchantBankMapperDTO.getVso()));
                responseDTO.setBankAccount(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankAccount()));
                responseDTO.setBankShortName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankShortName()));
                responseDTO.setUserBankName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getUserBankName()));
                responseDTO.setInvoiceNumber(requestPaymentDTO.getInvoiceNumber());
                responseDTO.setTotalAmount(requestPaymentDTO.getAmount());
                responseDTO.setVat(requestPaymentDTO.getVat());
                responseDTO.setVatAmount(requestPaymentDTO.getVatAmount());
                responseDTO.setInvoiceId(dto.getInvoiceId());

                result = responseDTO;
                httpStatus = HttpStatus.OK;
            } else {
                IInvoiceDTO invoiceDTO = invoiceService.getInvoiceRequestPayment(dto.getInvoiceId());
                List<IInvoiceItemDetailDTO> iInvoiceItemDetailDTOS = invoiceItemService
                        .getInvoiceItemsByIds(dto.getItemItemIds());
                if (invoiceDTO != null && iInvoiceItemDetailDTOS != null &&
                        iInvoiceItemDetailDTOS.size() == dto.getItemItemIds().size()) {
                    long totalAmountAfterVat = 0;
                    long totalAmount = 0;
                    long vatAmount = 0;
                    for (IInvoiceItemDetailDTO item : iInvoiceItemDetailDTOS) {
                        totalAmount += item.getTotalAmount();
                        vatAmount += item.getVatAmount();
                        totalAmountAfterVat += item.getAmountAfterVat();
                    }
                    MerchantBankMapperDTO merchantBankMapperDTO = getMerchantBankMapperDTO(invoiceDTO.getData());
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumberVQR = "VTS" + RandomCodeUtil.generateRandomId(10);
                    String otpPayment = RandomCodeUtil.generateOTP(6);
                    String content = traceId + " " + billNumberVQR;
                    List<TransactionWalletEntity> transactionWalletEntities = new ArrayList<>();

                    // create transaction_wallet_credit
                    TransactionWalletEntity walletEntityCredit = new TransactionWalletEntity();
                    UUID transWalletCreditUUID = UUID.randomUUID();
                    walletEntityCredit.setId(transWalletCreditUUID.toString());
                    walletEntityCredit.setAmount(totalAmountAfterVat + "");
                    walletEntityCredit.setBillNumber(billNumberVQR);
                    walletEntityCredit.setContent(content);
                    walletEntityCredit.setStatus(0);
                    walletEntityCredit.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    walletEntityCredit.setTimePaid(0);
                    walletEntityCredit.setTransType("C");
                    walletEntityCredit.setUserId(invoiceDTO.getUserId());
                    walletEntityCredit.setOtp("");
                    walletEntityCredit.setPaymentType(0);
                    walletEntityCredit.setPaymentMethod(1);
                    walletEntityCredit.setReferenceNumber(3 + "*" + invoiceDTO.getUserId() + "*" +
                            otpPayment + "*" + invoiceDTO.getBankId());
                    walletEntityCredit.setPhoneNoRC("");
                    walletEntityCredit.setData(invoiceDTO.getBankId());
                    walletEntityCredit.setRefId("");
                    transactionWalletEntities.add(walletEntityCredit);

                    // create transaction_wallet_debit
                    TransactionWalletEntity walletEntityDebit = new TransactionWalletEntity();
                    UUID transWalletUUID = UUID.randomUUID();
                    walletEntityDebit.setId(transWalletUUID.toString());
                    walletEntityDebit.setAmount(totalAmountAfterVat + "");
                    walletEntityDebit.setBillNumber("");
                    walletEntityDebit.setContent(content);
                    walletEntityDebit.setStatus(0);
                    walletEntityDebit.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    walletEntityDebit.setTimePaid(0);
                    walletEntityDebit.setTransType("D");
                    walletEntityDebit.setUserId(invoiceDTO.getUserId());
                    walletEntityDebit.setOtp(otpPayment);
                    walletEntityDebit.setPaymentType(3);
                    walletEntityDebit.setPaymentMethod(0);
                    walletEntityDebit.setReferenceNumber("");
                    walletEntityDebit.setPhoneNoRC("");
                    walletEntityDebit.setData(invoiceDTO.getBankId());
                    walletEntityDebit.setRefId(transWalletCreditUUID.toString());
                    transactionWalletEntities.add(walletEntityDebit);

                    // generate VQR

//                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
//                    String bankId = EnvironmentUtil.getBankIdRecharge();
                    IBankAccountInfoDTO bankAccountInfoDTO = accountBankReceiveService
                            .getAccountBankInfoById(bankId);
                    String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
                    String cai = EnvironmentUtil.getCAIRecharge();
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(cai);
                    vietQRGenerateDTO.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    vietQRGenerateDTO.setAmount(totalAmountAfterVat + "");
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

                    // insert transaction_receive
                    UUID transReceiveUUID = UUID.randomUUID();
                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                    transactionReceiveEntity.setAmount(totalAmountAfterVat);
                    transactionReceiveEntity.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    transactionReceiveEntity.setBankId(bankId);
                    transactionReceiveEntity.setContent(content);
                    transactionReceiveEntity.setRefId("");
                    transactionReceiveEntity.setStatus(0);
                    transactionReceiveEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
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

                    InvoiceTransactionEntity entity = new InvoiceTransactionEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setInvoiceId(dto.getInvoiceId());
                    entity.setInvoiceItemIds(mapper.writeValueAsString(dto.getItemItemIds()));
                    if (!StringUtil.isNullOrEmpty(dto.getBankIdRecharge())) {
                        entity.setBankIdRecharge(dto.getBankIdRecharge());
                    } else {
                        entity.setBankIdRecharge(bankIdRechargeDefault);
                    }
                    entity.setMid(invoiceDTO.getMerchantId());
                    entity.setBankId(invoiceDTO.getBankId());
                    entity.setUserId(invoiceDTO.getUserId());
                    entity.setStatus(0);
                    entity.setVat(invoiceDTO.getVat());
                    entity.setRefId(transReceiveUUID.toString());
                    entity.setInvoiceNumber(billNumberVQR);
                    entity.setTotalAmount(totalAmountAfterVat);
                    entity.setAmount(totalAmount);
                    entity.setVatAmount(vatAmount);
                    entity.setQrCode(qr);

                    invoiceTransactionService.insert(entity);
                    transactionWalletService.insertAll(transactionWalletEntities);
                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);

                    responseDTO.setQrCode(qr);
                    responseDTO.setTotalAmountAfterVat(totalAmountAfterVat);
                    responseDTO.setInvoiceName(invoiceDTO.getInvoiceName());
                    responseDTO.setMidName(merchantBankMapperDTO.getMerchantName());
                    responseDTO.setVso(merchantBankMapperDTO.getVso());
                    responseDTO.setBankAccount(merchantBankMapperDTO.getBankAccount());
                    responseDTO.setBankShortName(merchantBankMapperDTO.getBankShortName());
                    responseDTO.setUserBankName(merchantBankMapperDTO.getUserBankName());
                    responseDTO.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
                    responseDTO.setTotalAmount(totalAmount);
                    responseDTO.setVat(invoiceDTO.getVat());
                    responseDTO.setVatAmount(vatAmount);
                    responseDTO.setInvoiceId(dto.getInvoiceId());
                    result = responseDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: requestPayment: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice/detail/{invoiceId}")
    public ResponseEntity<Object> getInvoiceDetailById(
            @PathVariable String invoiceId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceDetailAdminDTO dto = new InvoiceDetailAdminDTO();
            IInvoiceDTO invoiceDTO = invoiceService.getInvoiceByInvoiceDetail(invoiceId);
            if (invoiceDTO != null) {
                dto.setInvoiceId(invoiceDTO.getInvoiceId());
                dto.setInvoiceName(invoiceDTO.getInvoiceName());
                dto.setInvoiceDescription(invoiceDTO.getInvoiceDescription() != null
                        ? invoiceDTO.getInvoiceDescription() : "");
                dto.setVat(invoiceDTO.getVat());
                dto.setVatAmount(invoiceDTO.getVatAmount());
                dto.setTotalAmount(invoiceDTO.getTotalAmount());
                dto.setTotalAmountAfterVat(invoiceDTO.getTotalAmountAfterVat());
                dto.setStatus(invoiceDTO.getStatus());

                List<IInvoiceItemDetailDTO> iInvoiceItemDetailDTOS = invoiceItemService
                        .getInvoiceItemsByInvoiceId(invoiceId);
                List<InvoiceItemDetailDTO> invoiceItemDetailDTOS =
                        iInvoiceItemDetailDTOS.stream().map(item -> {
                            InvoiceItemDetailDTO invoiceItemDetailDTO = new InvoiceItemDetailDTO();
                            invoiceItemDetailDTO.setInvoiceItemId(item.getInvoiceItemId());
                            invoiceItemDetailDTO.setInvoiceItemName(item.getInvoiceItemName() != null ? item.getInvoiceItemName() : "");
                            invoiceItemDetailDTO.setUnit(item.getUnit() != null ? item.getUnit() : "");
                            invoiceItemDetailDTO.setQuantity(item.getQuantity());
                            invoiceItemDetailDTO.setAmount(item.getAmount());
                            invoiceItemDetailDTO.setStatus(item.getStatus());
                            invoiceItemDetailDTO.setTimePaid(item.getTimePaid());
                            invoiceItemDetailDTO.setTotalAmount(item.getTotalAmount());
                            invoiceItemDetailDTO.setVat(item.getVat() != null ? item.getVat() : invoiceDTO.getVat());
                            invoiceItemDetailDTO.setVatAmount(item.getVatAmount() != null ? item.getVatAmount() :
                                    Math.round(item.getTotalAmount() * invoiceDTO.getVat() / 100));
                            invoiceItemDetailDTO.setTotalAmountAfterVat(item.getAmountAfterVat() != null ?
                                    item.getAmountAfterVat() : Math.round(item.getTotalAmount() * (1 +
                                    invoiceDTO.getVat() / 100)));
                            return invoiceItemDetailDTO;
                        }).collect(Collectors.toList());

                dto.setInvoiceItemDetailDTOS(invoiceItemDetailDTOS);

                long totalPaid = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 1)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();
                long totalUnpaid = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 0)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();

                dto.setTotalPaid(totalPaid);
                dto.setTotalUnpaid(totalUnpaid);

                List<BankReceivePaymentRequestDTO> bankReceivePaymentRequestDTOS = getListPaymentRequest(invoiceDTO.getBankIdRecharge());
                dto.setPaymentRequestDTOS(bankReceivePaymentRequestDTOS);

                List<ICustomerDetailDTO> iCustomerDetailDTOS = new ArrayList<>();
                if (StringUtil.isNullOrEmpty(invoiceDTO.getMerchantId())) {
                    iCustomerDetailDTOS =
                            accountBankReceiveService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                } else {
                    iCustomerDetailDTOS =
                            bankReceiveFeePackageService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                }
                List<CustomerDetailDTO> customerDetailDTOList =
                        iCustomerDetailDTOS.stream().map(item -> {
                            CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
                            customerDetailDTO.setBankAccount(StringUtil.removeMarkString(item.getBankAccount()));
                            customerDetailDTO.setEmail(StringUtil.getValueNullChecker(item.getEmail() != null ? item.getEmail() : ""));
                            customerDetailDTO.setPlatform(item.getPlatform());
                            customerDetailDTO.setVso(item.getVso());
                            customerDetailDTO.setMerchantName(item.getMerchantName());
                            if (item.getMmsActive()) {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                            } else {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                            }
                            customerDetailDTO.setBankShortName(StringUtil.removeMarkString(item.getBankShortName()));
                            customerDetailDTO.setUserBankName(StringUtil.removeMarkString(item.getUserBankName()));
                            customerDetailDTO.setPhoneNo(item.getPhoneNo());
                            return customerDetailDTO;
                        }).collect(Collectors.toList());

                dto.setCustomerDetailDTOS(customerDetailDTOList);

                List<IFeePackageDetailDTO> iFeePackageDetailDTOS =
                        bankReceiveFeePackageService.getFeePackageDetail(invoiceDTO.getBankId());

                List<FeePackageDetailDTO> feePackageDetailDTOS =
                        iFeePackageDetailDTOS.stream().map(item -> {
                            FeePackageDetailDTO feePackageDetailDTO = new FeePackageDetailDTO();
                            feePackageDetailDTO.setFeePackage(item.getFeePackage());
                            feePackageDetailDTO.setAnnualFee(item.getAnnualFee());
                            feePackageDetailDTO.setFixFee(item.getFixFee());
                            feePackageDetailDTO.setRecordType(item.getRecordType());
                            feePackageDetailDTO.setPercentFee(item.getPercentFee());
                            feePackageDetailDTO.setVat(item.getVat());
                            return feePackageDetailDTO;
                        }).collect(Collectors.toList());

                dto.setFeePackageDetailDTOS(feePackageDetailDTOS);
            }
            result = dto;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceDetailById: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice/{userId}")
    public ResponseEntity<Object> getInvoicesByUser(
            @PathVariable String userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String bankId,
            @RequestParam int status,
            @RequestParam int filterBy,
            @RequestParam String time
    ) {
        Object result = null;
        PageResDTO response = new PageResDTO();
        HttpStatus httpStatus = null;
        if (filterBy == 9) {
            time = "";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            int totalElement = 0;
            List<InvoiceResponseDTO> data = new ArrayList<>();
            List<IInvoiceResponseDTO> dtos = new ArrayList<>();
            int offset = (page - 1) * size;
            List<Integer> statuses = new ArrayList<>();
            statuses.add(status);
            if (status == 0) {
                statuses.add(3);
            }
            if (bankId == null || bankId.isEmpty()) {
                if (time == null || time.isEmpty()) {
                    dtos = invoiceService.getInvoiceByUserId(userId, statuses, offset, size);
                    totalElement = invoiceService.countInvoiceByUserId(userId, statuses);
                } else {
                    dtos = invoiceService.getInvoiceByUserIdAndMonth(userId, statuses, time, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndMonth(userId, statuses, time);
                }
            } else {
                if (time == null || time.isEmpty()) {
                    dtos = invoiceService.getInvoiceByUserIdAndBankId(userId, statuses, bankId, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndBankId(userId, statuses, bankId);
                } else {
                    dtos = invoiceService.getInvoiceByUserIdAndBankIdAndMonth(userId, statuses, bankId, time, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndBankIdAndMonth(userId, statuses, bankId, time);
                }
            }
            data = dtos.stream().map(item -> {
                InvoiceResponseDTO dto = new InvoiceResponseDTO();
                dto.setInvoiceId(item.getInvoiceId());
                dto.setInvoiceName(item.getInvoiceName());
                dto.setBillNumber(item.getInvoiceNumber());
                dto.setInvoiceNumber(item.getInvoiceNumber());
                dto.setTimeCreated(item.getTimeCreated());
                dto.setTimePaid(item.getTimePaid());
                dto.setStatus(item.getStatus());
                dto.setBankId(item.getBankId());
                MerchantBankMapperDTO merchantBankMapperDTO = getMerchantBankMapperDTO(item.getData());
                dto.setVso(StringUtil.getValueNullChecker(merchantBankMapperDTO.getVso()));
                dto.setMidName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getMerchantName()));
                dto.setBankShortName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankShortName()));
                dto.setBankAccount(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankAccount()));
                dto.setUserBankName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getUserBankName()));
                dto.setBankAccountForPayment("");
                dto.setUserBankNameForPayment("");
                dto.setBankNameForPayment("");
                dto.setBankCodeForPayment("");
                dto.setQrCode("");
                dto.setTotalAmount(item.getTotalAmount());
                return dto;
            }).collect(Collectors.toList());
            PageDTO pageDTO = new PageDTO();
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageDTO.setTotalElement(totalElement);
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            response.setMetadata(pageDTO);
            response.setData(data);
            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoicesByUser: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice-detail/{invoiceId}")
    public ResponseEntity<Object> getInvoiceDetailByInvoiceId(
            @PathVariable String invoiceId
    ) {
        Object result = null;
        InvoiceDetailDTO response = new InvoiceDetailDTO();
        List<IInvoiceItemResponseDTO> items = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<InvoiceItemResDTO> data = new ArrayList<>();
            IInvoiceDetailDTO dto = invoiceService.getInvoiceDetailById(invoiceId);
            items = invoiceItemService.getInvoiceByInvoiceId(invoiceId);
            data = items.stream().map(item -> {
                InvoiceItemResDTO itemResDTO = new InvoiceItemResDTO();
                itemResDTO.setInvoiceItemId(item.getInvoiceItemId());
                itemResDTO.setInvoiceItemName(item.getInvoiceItemName());
                itemResDTO.setQuantity(item.getQuantity());
                itemResDTO.setTotalItemAmount(item.getTotalItemAmount());
                itemResDTO.setItemAmount(item.getItemAmount());
                return itemResDTO;
            }).collect(Collectors.toList());
            response.setInvoiceId(dto.getInvoiceId());
            response.setBillNumber(dto.getInvoiceNumber());
            response.setInvoiceNumber(dto.getInvoiceNumber());
            response.setInvoiceName(dto.getInvoiceName());
            response.setTimeCreated(dto.getTimeCreated());
            response.setTimePaid(dto.getTimePaid());
            response.setStatus(dto.getStatus());
            response.setVatAmount(dto.getVatAmount());
            response.setBankId(dto.getBankId());
            response.setAmount(dto.getAmount());
            response.setVat(dto.getVat());
            try {
                BankAccountInfoDTO bankAccountInfoDTO = mapper.readValue(dto.getData(), BankAccountInfoDTO.class);
                if (bankAccountInfoDTO != null) {
                    response.setBankAccount(bankAccountInfoDTO.getBankAccount() != null ?
                            bankAccountInfoDTO.getBankAccount() : "");
                    response.setBankShortName(bankAccountInfoDTO.getBankShortName() != null ?
                            bankAccountInfoDTO.getBankShortName() : "");
                }
            } catch (JsonProcessingException e) {
                response.setBankAccount("");
                response.setBankShortName("");
            }
            String cai = EnvironmentUtil.getCAIRecharge();
            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
            vietQRGenerateDTO.setCaiValue(cai);
            String bankAccount = EnvironmentUtil.getBankAccountRecharge();
            String bankCode = EnvironmentUtil.getBankCodeRecharge();
            String bankName = EnvironmentUtil.getBankNameRecharge();
            String userBankName = EnvironmentUtil.getUserBankNameRecharge();
            vietQRGenerateDTO.setBankAccount(bankAccount);
            vietQRGenerateDTO.setAmount(dto.getTotalAmount() + "");
            vietQRGenerateDTO.setContent(dto.getContent());
            String qrCode = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
            response.setQrCode(qrCode);
            response.setBankAccountForPayment(bankAccount);
            response.setUserBankNameForPayment(userBankName);
            response.setBankNameForPayment(bankName);
            response.setBankCodeForPayment(bankCode);
            response.setTotalAmount(dto.getTotalAmount());
            response.setItems(data);
            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceDetailByInvoiceId: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice-user/payment")
    public ResponseEntity<Object> requestPaymentUser(
            @Valid @RequestBody PaymentRequestDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            PaymentRequestResponseDTO responseDTO = new PaymentRequestResponseDTO();
            String bankIdRechargeDefault = systemSettingService.getBankIdRechargeDefault();
            ObjectMapper mapper = new ObjectMapper();
            String itemIds = getSortedListString(dto.getItemItemIds());
            System.out.println(dto.getInvoiceId());
            System.out.println(itemIds.toString());
            InvoiceRequestPaymentDTO requestPaymentDTO = invoiceTransactionService.getInvoiceRequestPayment(
                    dto.getInvoiceId(),
                    itemIds
            );
            if (requestPaymentDTO != null) {
                MerchantBankMapperDTO merchantBankMapperDTO = null;
                merchantBankMapperDTO = getMerchantBankMapperDTO(requestPaymentDTO.getData());
                responseDTO.setQrCode(requestPaymentDTO.getQrCode());
                responseDTO.setTotalAmountAfterVat(requestPaymentDTO.getTotalAMount());
                responseDTO.setInvoiceName(requestPaymentDTO.getInvoiceName());
                responseDTO.setMidName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getMerchantName()));
                responseDTO.setVso(StringUtil.getValueNullChecker(merchantBankMapperDTO.getVso()));
                responseDTO.setBankAccount(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankAccount()));
                responseDTO.setBankShortName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getBankShortName()));
                responseDTO.setUserBankName(StringUtil.getValueNullChecker(merchantBankMapperDTO.getUserBankName()));
                responseDTO.setInvoiceNumber(requestPaymentDTO.getInvoiceNumber());
                responseDTO.setTotalAmount(requestPaymentDTO.getAmount());
                responseDTO.setVat(requestPaymentDTO.getVat());
                responseDTO.setVatAmount(requestPaymentDTO.getVatAmount());
                responseDTO.setInvoiceId(dto.getInvoiceId());
                result = responseDTO;
                httpStatus = HttpStatus.OK;
            } else {
                IInvoiceDTO invoiceDTO = invoiceService.getInvoiceRequestPayment(dto.getInvoiceId());
                List<IInvoiceItemDetailDTO> iInvoiceItemDetailDTOS = invoiceItemService
                        .getInvoiceItemsByIds(dto.getItemItemIds());
                if (invoiceDTO != null && iInvoiceItemDetailDTOS != null &&
                        iInvoiceItemDetailDTOS.size() == dto.getItemItemIds().size()) {
                    long totalAmountAfterVat = 0;
                    long totalAmount = 0;
                    long vatAmount = 0;
                    for (IInvoiceItemDetailDTO item : iInvoiceItemDetailDTOS) {
                        totalAmount += item.getTotalAmount();
                        vatAmount += item.getVatAmount();
                        totalAmountAfterVat += item.getAmountAfterVat();
                    }
                    MerchantBankMapperDTO merchantBankMapperDTO = getMerchantBankMapperDTO(invoiceDTO.getData());
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumberVQR = "VTS" + RandomCodeUtil.generateRandomId(10);
                    String otpPayment = RandomCodeUtil.generateOTP(6);
                    String content = traceId + " " + billNumberVQR;
                    List<TransactionWalletEntity> transactionWalletEntities = new ArrayList<>();

                    // create transaction_wallet_credit
                    TransactionWalletEntity walletEntityCredit = new TransactionWalletEntity();
                    UUID transWalletCreditUUID = UUID.randomUUID();
                    walletEntityCredit.setId(transWalletCreditUUID.toString());
                    walletEntityCredit.setAmount(totalAmountAfterVat + "");
                    walletEntityCredit.setBillNumber(billNumberVQR);
                    walletEntityCredit.setContent(content);
                    walletEntityCredit.setStatus(0);
                    walletEntityCredit.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    walletEntityCredit.setTimePaid(0);
                    walletEntityCredit.setTransType("C");
                    walletEntityCredit.setUserId(invoiceDTO.getUserId());
                    walletEntityCredit.setOtp("");
                    walletEntityCredit.setPaymentType(0);
                    walletEntityCredit.setPaymentMethod(1);
                    walletEntityCredit.setReferenceNumber(3 + "*" + invoiceDTO.getUserId() + "*" +
                            otpPayment + "*" + invoiceDTO.getBankId());
                    walletEntityCredit.setPhoneNoRC("");
                    walletEntityCredit.setData(invoiceDTO.getBankId());
                    walletEntityCredit.setRefId("");
                    transactionWalletEntities.add(walletEntityCredit);

                    // create transaction_wallet_debit
                    TransactionWalletEntity walletEntityDebit = new TransactionWalletEntity();
                    UUID transWalletUUID = UUID.randomUUID();
                    walletEntityDebit.setId(transWalletUUID.toString());
                    walletEntityDebit.setAmount(totalAmountAfterVat + "");
                    walletEntityDebit.setBillNumber(billNumberVQR);
                    walletEntityDebit.setContent(content);
                    walletEntityDebit.setStatus(0);
                    walletEntityDebit.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    walletEntityDebit.setTimePaid(0);
                    walletEntityDebit.setTransType("D");
                    walletEntityDebit.setUserId(invoiceDTO.getUserId());
                    walletEntityDebit.setOtp(otpPayment);
                    walletEntityDebit.setPaymentType(3);
                    walletEntityDebit.setPaymentMethod(0);
                    walletEntityDebit.setReferenceNumber("");
                    walletEntityDebit.setPhoneNoRC("");
                    walletEntityDebit.setData(invoiceDTO.getBankId());
                    walletEntityDebit.setRefId(transWalletCreditUUID.toString());
                    transactionWalletEntities.add(walletEntityDebit);

                    // generate VQR
                    IBankAccountInfoDTO bankAccountInfoDTO = accountBankReceiveService
                            .getAccountBankInfoById(bankIdRechargeDefault);
                    String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
                    String cai = EnvironmentUtil.getCAIRecharge();
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(cai);
                    vietQRGenerateDTO.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    vietQRGenerateDTO.setAmount(totalAmountAfterVat + "");
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

                    // insert transaction_receive
                    UUID transReceiveUUID = UUID.randomUUID();
                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                    transactionReceiveEntity.setAmount(totalAmountAfterVat);
                    transactionReceiveEntity.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    transactionReceiveEntity.setBankId(bankIdRechargeDefault);
                    transactionReceiveEntity.setContent(content);
                    transactionReceiveEntity.setRefId("");
                    transactionReceiveEntity.setStatus(0);
                    transactionReceiveEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
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

                    InvoiceTransactionEntity entity = new InvoiceTransactionEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setInvoiceId(dto.getInvoiceId());
                    entity.setInvoiceItemIds(mapper.writeValueAsString(dto.getItemItemIds()));
                    if (!StringUtil.isNullOrEmpty(dto.getBankIdRecharge())) {
                        entity.setBankIdRecharge(dto.getBankIdRecharge());
                    } else {
                        entity.setBankIdRecharge(bankIdRechargeDefault);
                    }
                    entity.setMid(invoiceDTO.getMerchantId());
                    entity.setBankId(invoiceDTO.getBankId());
                    entity.setUserId(invoiceDTO.getUserId());
                    entity.setStatus(0);
                    entity.setVat(invoiceDTO.getVat());
                    entity.setRefId(transReceiveUUID.toString());
                    entity.setInvoiceNumber(billNumberVQR);
                    entity.setTotalAmount(totalAmountAfterVat);
                    entity.setAmount(totalAmount);
                    entity.setVatAmount(vatAmount);
                    entity.setQrCode(qr);

                    invoiceTransactionService.insert(entity);
                    transactionWalletService.insertAll(transactionWalletEntities);
                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);

                    responseDTO.setQrCode(qr);
                    responseDTO.setTotalAmountAfterVat(totalAmountAfterVat);
                    responseDTO.setInvoiceName(invoiceDTO.getInvoiceName());
                    responseDTO.setMidName(merchantBankMapperDTO.getMerchantName());
                    responseDTO.setVso(merchantBankMapperDTO.getVso());
                    responseDTO.setBankAccount(merchantBankMapperDTO.getBankAccount());
                    responseDTO.setBankShortName(merchantBankMapperDTO.getBankShortName());
                    responseDTO.setUserBankName(merchantBankMapperDTO.getUserBankName());
                    responseDTO.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
                    responseDTO.setTotalAmount(totalAmount);
                    responseDTO.setVat(invoiceDTO.getVat());
                    responseDTO.setVatAmount(vatAmount);
                    responseDTO.setInvoiceId(dto.getInvoiceId());
                    result = responseDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: requestPaymentUser: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("invoice-unpaid/{userId}")
    public ResponseEntity<InvoiceUnpaidUserStaDTO> getTotalInvoiceUnpaidByUserId(
            @PathVariable String userId
    ) {
        InvoiceUnpaidUserStaDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = new InvoiceUnpaidUserStaDTO();
            InvoiceUnpaidStatisticDTO dto = invoiceService.getTotalInvoiceUnpaidByUserId(userId);
            if (dto != null) {
                result.setTotalInvoice(dto.getTotalInvoice());
                result.setTotalUnpaid(dto.getTotalMoney());
                result.setUserId(dto.getUserId());
            } else {
                result.setTotalInvoice(0);
                result.setTotalUnpaid(0);
                result.setUserId(userId);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getTotalInvoiceUnpaidByUserId: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice-detail/web/{invoiceId}")
    public ResponseEntity<Object> getInvoiceDetailWebByInvoiceId(
            @PathVariable String invoiceId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceDetailWebDTO dto = new InvoiceDetailWebDTO();
            IInvoiceDTO invoiceDTO = invoiceService.getInvoiceByInvoiceDetail(invoiceId);
            if (invoiceDTO != null) {
                dto.setInvoiceId(invoiceDTO.getInvoiceId());
                dto.setInvoiceName(invoiceDTO.getInvoiceName());
                dto.setInvoiceDescription(StringUtil.getValueNullChecker(invoiceDTO.getInvoiceDescription()));
                dto.setVat(invoiceDTO.getVat());
                dto.setVatAmount(invoiceDTO.getVatAmount());
                dto.setTotalAmount(invoiceDTO.getTotalAmount());
                dto.setTotalAmountAfterVat(invoiceDTO.getTotalAmountAfterVat());
                dto.setStatus(invoiceDTO.getStatus());

                List<IInvoiceItemDetailDTO> iInvoiceItemDetailDTOS = invoiceItemService
                        .getInvoiceItemsByInvoiceId(invoiceId);
                List<InvoiceItemDetailDTO> invoiceItemDetailDTOS =
                        iInvoiceItemDetailDTOS.stream().map(item -> {
                            InvoiceItemDetailDTO invoiceItemDetailDTO = new InvoiceItemDetailDTO();
                            invoiceItemDetailDTO.setInvoiceItemId(item.getInvoiceItemId());
                            invoiceItemDetailDTO.setInvoiceItemName(item.getInvoiceItemName());
                            invoiceItemDetailDTO.setUnit(item.getUnit() != null ? item.getUnit() : "");
                            invoiceItemDetailDTO.setQuantity(item.getQuantity());
                            invoiceItemDetailDTO.setAmount(item.getAmount());
                            invoiceItemDetailDTO.setTotalAmount(item.getTotalAmount());
                            invoiceItemDetailDTO.setVat(StringUtil.getValueNullChecker(item.getVat()));
                            invoiceItemDetailDTO.setVatAmount(item.getVatAmount());
                            invoiceItemDetailDTO.setTotalAmountAfterVat(item.getAmountAfterVat());
                            invoiceItemDetailDTO.setStatus(item.getStatus());
                            invoiceItemDetailDTO.setTimePaid(item.getTimePaid());
                            return invoiceItemDetailDTO;
                        }).collect(Collectors.toList());
                long pendingAmount = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 0 && dto.getStatus() != 1)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();
                long completeAmount = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 1 || dto.getStatus() == 1)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();
                dto.setPendingAmount(pendingAmount);
                dto.setCompleteAmount(completeAmount);
                dto.setInvoiceItemDetailDTOS(invoiceItemDetailDTOS);

                List<ICustomerDetailDTO> iCustomerDetailDTOS = new ArrayList<>();
                if (StringUtil.isNullOrEmpty(invoiceDTO.getMerchantId())) {
                    iCustomerDetailDTOS =
                            accountBankReceiveService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                } else {
                    iCustomerDetailDTOS =
                            bankReceiveFeePackageService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                }
                List<CustomerDetailDTO> customerDetailDTOList =
                        iCustomerDetailDTOS.stream().map(item -> {
                            CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
                            customerDetailDTO.setBankAccount(StringUtil.removeMarkString(item.getBankAccount()));
                            customerDetailDTO.setEmail(StringUtil.getValueNullChecker(item.getEmail()));
                            customerDetailDTO.setPlatform(item.getPlatform());
                            customerDetailDTO.setVso(item.getVso());
                            customerDetailDTO.setMerchantName(item.getMerchantName());
                            if (item.getMmsActive()) {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                            } else {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                            }
                            customerDetailDTO.setBankShortName(StringUtil.removeMarkString(item.getBankShortName()));
                            customerDetailDTO.setUserBankName(StringUtil.removeMarkString(item.getUserBankName()));
                            customerDetailDTO.setPhoneNo(item.getPhoneNo());
                            return customerDetailDTO;
                        }).collect(Collectors.toList());

                dto.setCustomerDetailDTOS(customerDetailDTOList);

                List<IFeePackageDetailDTO> iFeePackageDetailDTOS =
                        bankReceiveFeePackageService.getFeePackageDetail(invoiceDTO.getBankId());

                List<FeePackageDetailDTO> feePackageDetailDTOS =
                        iFeePackageDetailDTOS.stream().map(item -> {
                            FeePackageDetailDTO feePackageDetailDTO = new FeePackageDetailDTO();
                            feePackageDetailDTO.setFeePackage(item.getFeePackage());
                            feePackageDetailDTO.setAnnualFee(item.getAnnualFee());
                            feePackageDetailDTO.setFixFee(item.getFixFee());
                            feePackageDetailDTO.setRecordType(item.getRecordType());
                            feePackageDetailDTO.setPercentFee(item.getPercentFee());
                            feePackageDetailDTO.setVat(item.getVat());
                            return feePackageDetailDTO;
                        }).collect(Collectors.toList());
                if (feePackageDetailDTOS.isEmpty()) {
                    feePackageDetailDTOS.add(new FeePackageDetailDTO(systemSettingService.getVatSystemSetting()));
                }
                dto.setFeePackageDetailDTOS(feePackageDetailDTOS);
            }
            result = dto;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceDetailWebByInvoiceId: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice/invoice-item")
    public ResponseEntity<Object> getInvoiceItem(
            @RequestParam String bankId,
            @RequestParam String merchantId,
            @RequestParam int type,
            @RequestParam String time,
            @RequestParam double vat
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String monthYear = DateTimeUtil.getFormatMonthYear(time);
            InvoiceCreateItemDTO data = null;
            IInvoiceItemCreateDTO feePackage;
            switch (type) {
                // annual fee
                case 0:
                    //String processDate = DateTimeUtil.getFormatMonthYear(time);;
                    String processDate = time.replaceAll("-", "");
                    int checkInvoiceItem = invoiceItemService.checkInvoiceItemExist(bankId, merchantId, type, processDate);

                    if(checkInvoiceItem == 0){
                        data = new InvoiceCreateItemDTO();
                        data.setItemId(monthYear + type);
                        data.setVat(vat);
                        data.setTimeProcess(time);
                        data.setType(type);
                        data.setContent(EnvironmentUtil.getVietQrNameAnnualFee() + monthYear);
                        data.setUnit(EnvironmentUtil.getMonthUnitNameVn());
                        data.setQuantity(1);
                        feePackage = bankReceiveFeePackageService.getFeePackageByBankId(bankId);
                        if (feePackage != null) {
                            data.setAmount(feePackage.getAnnualFee());
                            data.setTotalAmount(feePackage.getAnnualFee());
                            data.setVatAmount(Math.round(feePackage.getVat() / 100 * feePackage.getAnnualFee()));
                            data.setAmountAfterVat(data.getTotalAmount() + data.getVatAmount());
                        }
                        break;
                    }else{
                        result = new ResponseMessageDTO("FAILED", "E05");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                    }

                // phi giao dich
                case 1:
                    data = new InvoiceCreateItemDTO();
                    data.setItemId(monthYear + type);
                    data.setVat(vat);
                    data.setTimeProcess(time);
                    data.setType(type);
                    data.setContent(EnvironmentUtil.getVietQrNameTransFee() + monthYear);
                    data.setUnit(EnvironmentUtil.getMonthUnitNameVn());
                    data.setQuantity(1);

                    feePackage = bankReceiveFeePackageService.getFeePackageByBankId(bankId);
                    List<TransReceiveInvoicesDTO> transactionReceiveEntities =
                            transactionReceiveService.getTransactionReceiveByBankId(bankId, time);
                    List<TransReceiveInvoicesDTO> transReceiveInvoicesInvoices = new ArrayList<>();
                    double totalAmountRaw = 0.D;
                    if (feePackage != null) {
                        int isTotal = feePackage.getRecordType();
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
                    data.setAmount(totalAmount);
                    data.setTotalAmount(totalAmount);
                    data.setVatAmount(Math.round(vat / 100 * totalAmount));
                    data.setAmountAfterVat(Math.round((vat + 100) / 100 * totalAmount));
                    break;
                // phi khac
                case 9:
                    data = new InvoiceCreateItemDTO();
                    data.setItemId(UUID.randomUUID().toString());
                    data.setVat(vat);
                    data.setTimeProcess(time);
                    data.setType(type);
                    data.setContent(EnvironmentUtil.getVietQrNameAnotherFee());
                    break;
                default:
                    break;
            }
            result = data;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("/invoice/create")
    public ResponseEntity<Object> getInvoiceByItem(
            @Valid @RequestBody InvoiceCreateUpdateDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long totalAmount = 0;
            long totalAmountAfterVat = 0;
            long totalVatAmount = 0;
            ObjectMapper mapper = new ObjectMapper();
            InvoiceEntity entity = new InvoiceEntity();
            LocalDateTime current = LocalDateTime.now();
            long time = current.toEpochSecond(ZoneOffset.UTC);
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
            // lấy uerID từ bankID trong account-bank-receive để push notification cho USERID ở đây
            String userIdByBankId = accountBankReceiveService.getUserIdByBankId(dto.getBankId());

            IMerchantBankMapperDTO merchantMapper = null;
            IBankReceiveMapperDTO bankReceiveMapperDTO = null;
            if (StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                bankReceiveMapperDTO = accountBankReceiveService
                        .getMerchantBankMapper(dto.getBankId());
            } else {
                merchantMapper = bankReceiveFeePackageService
                        .getMerchantBankMapper(dto.getMerchantId(), dto.getBankId());
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
                merchantBankMapperDTO.setPhoneNo(merchantBankMapperDTO.getPhoneNo());

                entity.setUserId(merchantMapper.getUserId());
            } else if (bankReceiveMapperDTO != null) {
                merchantBankMapperDTO.setUserBankName(bankReceiveMapperDTO.getUserBankName());
                merchantBankMapperDTO.setMerchantName("");
                merchantBankMapperDTO.setVso("");
                merchantBankMapperDTO.setEmail(StringUtil.getValueNullChecker(bankReceiveMapperDTO.getEmail()));
                merchantBankMapperDTO.setBankAccount(bankReceiveMapperDTO.getBankAccount());
                merchantBankMapperDTO.setBankShortName(bankReceiveMapperDTO.getBankShortName());
                merchantBankMapperDTO.setPhoneNo(bankReceiveMapperDTO.getPhoneNo());
                entity.setUserId(bankReceiveMapperDTO.getUserId());
            } else {
                entity.setUserId("");
            }
            try {
                entity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
                entity.setDataType(1);
            } catch (Exception ignored) {
                entity.setData("");
                entity.setDataType(9);
            }
            List<String> itemIds = new ArrayList<>();
            List<InvoiceItemEntity> invoiceItemEntities = new ArrayList<>();
            for (InvoiceItemCreateDTO item : dto.getItems()) {
                InvoiceItemEntity invoiceItemEntity = new InvoiceItemEntity();
                String invoiceItemId = UUID.randomUUID().toString();
                itemIds.add(invoiceItemId);
                invoiceItemEntity.setId(invoiceItemId);
                invoiceItemEntity.setInvoiceId(invoiceId.toString());
                invoiceItemEntity.setAmount(item.getAmount());
                invoiceItemEntity.setQuantity(item.getQuantity());
                invoiceItemEntity.setTotalAmount(item.getTotalAmount());
                invoiceItemEntity.setTotalAfterVat(item.getAmountAfterVat());
                invoiceItemEntity.setName(item.getContent());
                invoiceItemEntity.setDescription(item.getContent());

                switch (item.getType()) {
                    case 0:
                        invoiceItemEntity.setType(0);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameAnnualFee());
                        String processDate = item.getTimeProcess().replaceAll("-", "");
                        int checkItem = invoiceItemService.checkInvoiceItemExist(dto.getBankId(), dto.getMerchantId(),
                                0, processDate);
                        if (checkItem == 0) {
                            invoiceItemEntities.add(invoiceItemEntity);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E140");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            return new ResponseEntity<>(result, httpStatus);
                        }
                        break;
                    case 1:
                        invoiceItemEntity.setType(1);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameTransFee());

                        //check condition BankID, type 0 or 1 and processDate is existing
                        String processDate2 = item.getTimeProcess().replaceAll("-", "");
                        int checkItem2 = invoiceItemService.checkInvoiceItemExist(dto.getBankId(), dto.getMerchantId(),
                                1, processDate2);
                        if (checkItem2 == 0) {
                            invoiceItemEntities.add(invoiceItemEntity);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E140");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            return new ResponseEntity<>(result, httpStatus);
                        }
                        break;
                    case 9:
                        invoiceItemEntity.setType(9);
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameAnotherFee());
                        invoiceItemEntities.add(invoiceItemEntity);
                        break;
                }

                invoiceItemEntity.setUnit(item.getUnit());
                invoiceItemEntity.setVat(item.getVat());
                invoiceItemEntity.setVatAmount(item.getVatAmount());
                invoiceItemEntity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
                invoiceItemEntity.setDataType(1);
                String processDate = item.getTimeProcess().replaceAll("-", "");
                if (item.getTimeProcess() != null) {
                    invoiceItemEntity.setProcessDate(processDate);
                }
                totalAmount += item.getTotalAmount();
                totalVatAmount += item.getVatAmount();
                totalAmountAfterVat += item.getAmountAfterVat();
            }
            entity.setTotalAmount(totalAmountAfterVat);
            entity.setAmount(totalAmount);
            entity.setVatAmount(totalVatAmount);
            entity.setVat(dto.getVat());

            // create transaction_wallet_debit
            UUID transWalletUUID = UUID.randomUUID();
            // generate VQR
            String bankAccount = EnvironmentUtil.getBankAccountRecharge();
            String bankId = EnvironmentUtil.getBankIdRecharge();
            String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
            String cai = EnvironmentUtil.getCAIRecharge();
            long finalTotalAmountAfterVat1 = totalAmountAfterVat;
            long finalTotalAmount = totalAmount;
            long finalTotalVatAmount = totalVatAmount;
            Thread thread = new Thread(() -> {
                String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                String otpPayment = RandomCodeUtil.generateOTP(6);
                String content = traceId + " " + invoiceNumber;
                List<TransactionWalletEntity> transactionWalletEntities = new ArrayList<>();

                // create transaction_wallet_credit
                TransactionWalletEntity walletEntityCredit = new TransactionWalletEntity();
                UUID transWalletCreditUUID = UUID.randomUUID();
                walletEntityCredit.setId(transWalletCreditUUID.toString());
                walletEntityCredit.setAmount(finalTotalAmountAfterVat1 + "");
                walletEntityCredit.setBillNumber(invoiceNumber);
                walletEntityCredit.setContent(content);
                walletEntityCredit.setStatus(0);
                walletEntityCredit.setTimeCreated(time);
                walletEntityCredit.setTimePaid(0);
                walletEntityCredit.setTransType("C");
                walletEntityCredit.setUserId(entity.getUserId());
                walletEntityCredit.setOtp("");
                walletEntityCredit.setPaymentType(0);
                walletEntityCredit.setPaymentMethod(1);
                walletEntityCredit.setReferenceNumber(3 + "*" + entity.getUserId() + "*" +
                        otpPayment + "*" + entity.getBankId());
                walletEntityCredit.setPhoneNoRC("");
                walletEntityCredit.setData(dto.getBankId());
                walletEntityCredit.setRefId("");
                transactionWalletEntities.add(walletEntityCredit);
                TransactionWalletEntity walletEntityDebit = new TransactionWalletEntity();
                walletEntityDebit.setId(transWalletUUID.toString());
                walletEntityDebit.setAmount(finalTotalAmountAfterVat1 + "");
                walletEntityDebit.setBillNumber("");
                walletEntityDebit.setContent(content);
                walletEntityDebit.setStatus(0);
                walletEntityDebit.setTimeCreated(time);
                walletEntityDebit.setTimePaid(0);
                walletEntityDebit.setTransType("D");
                walletEntityDebit.setUserId(entity.getUserId());
                walletEntityDebit.setOtp(otpPayment);
                walletEntityDebit.setPaymentType(3);
                walletEntityDebit.setPaymentMethod(0);
                walletEntityDebit.setReferenceNumber("");
                walletEntityDebit.setPhoneNoRC("");
                walletEntityDebit.setData(dto.getBankId());
                walletEntityDebit.setRefId(transWalletCreditUUID.toString());

                transactionWalletEntities.add(walletEntityDebit);

                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                vietQRGenerateDTO.setCaiValue(cai);
                vietQRGenerateDTO.setBankAccount(bankAccount);
                vietQRGenerateDTO.setAmount(finalTotalAmountAfterVat1 + "");
                vietQRGenerateDTO.setContent(content);
                String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

                // insert transaction_receive
                UUID transReceiveUUID = UUID.randomUUID();
                TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                transactionReceiveEntity.setId(transReceiveUUID.toString());
                transactionReceiveEntity.setAmount(finalTotalAmountAfterVat1);
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
                transactionReceiveEntity.setOrderId(invoiceNumber);
                transactionReceiveEntity.setSign("");
                transactionReceiveEntity.setCustomerBankAccount("");
                transactionReceiveEntity.setCustomerBankCode("");
                transactionReceiveEntity.setCustomerName("");
                transactionReceiveEntity.setTerminalCode(EnvironmentUtil.getVietQrActiveKey());
                transactionReceiveEntity.setUserId(userIdHost);
                transactionReceiveEntity.setNote("");
                transactionReceiveEntity.setTransStatus(0);
                transactionReceiveEntity.setQrCode("");
                transactionReceiveEntity.setUrlLink("");

                InvoiceTransactionEntity invoiceTransactionEntity = new InvoiceTransactionEntity();
                invoiceTransactionEntity.setId(UUID.randomUUID().toString());
                invoiceTransactionEntity.setInvoiceId(invoiceId.toString());
                try {
                    invoiceTransactionEntity.setInvoiceItemIds(mapper.writeValueAsString(itemIds));
                } catch (JsonProcessingException e) {
                    invoiceTransactionEntity.setInvoiceItemIds("[]");
                }
                String bankIdRechargeDefault = systemSettingService.getBankIdRechargeDefault();
                invoiceTransactionEntity.setBankIdRecharge(StringUtil.getValueNullChecker(bankIdRechargeDefault));
                invoiceTransactionEntity.setMid(dto.getMerchantId());
                invoiceTransactionEntity.setBankId(dto.getBankId());
                invoiceTransactionEntity.setUserId(userIdByBankId);
                invoiceTransactionEntity.setStatus(0);
                invoiceTransactionEntity.setVat(dto.getVat());
                invoiceTransactionEntity.setRefId(transReceiveUUID.toString());
                invoiceTransactionEntity.setInvoiceNumber(invoiceNumber);
                invoiceTransactionEntity.setTotalAmount(finalTotalAmountAfterVat1);
                invoiceTransactionEntity.setAmount(finalTotalAmount);
                invoiceTransactionEntity.setVatAmount(finalTotalVatAmount);
                invoiceTransactionEntity.setQrCode(qr);

                invoiceTransactionService.insert(invoiceTransactionEntity);
                transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                transactionWalletService.insertAll(transactionWalletEntities);
            });
            thread.start();

            entity.setRefId(transWalletUUID.toString());
            entity.setBankIdRecharge(bankId);
            invoiceItemService.insertAll(invoiceItemEntities);
            invoiceService.insert(entity);

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
            notiEntity.setUserId(userIdByBankId);
            notiEntity.setData(userIdByBankId);
            Map<String, String> datas = new HashMap<>();
            datas.put("notificationType", notiType);
            datas.put("notificationId", notificationUUID.toString());
            datas.put("status", "1");
            datas.put("bankCode", "MB");
            datas.put("terminalCode", "");
            datas.put("terminalName", "");
            datas.put("html", "<div><span style=\"font-size: 12;\">Bạn có 1 hóa đơn<strong> " + StringUtil.formatNumberAsString(finalTotalAmountAfterVat1 + "") + " VND " +
                    "</strong><br>cần thanh toán!</span></div>");
            datas.put("invoiceId", entity.getInvoiceId());  //invoice ID
            datas.put("time", time + "");
            datas.put("invoiceName", entity.getName());
            datas.put("status", 1 + "");
                pushNotification(title, message, notiEntity, datas, notiEntity.getUserId());
            });
            thread2.start();
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E140");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice/remove")
    public ResponseEntity<Object> removeInvoiceById(
            @Valid @RequestBody InvoiceRemoveDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String check = invoiceService.checkExistedInvoice(dto.getInvoiceId());
            if (!StringUtil.isNullOrEmpty(check)) {
                invoiceItemService.removeByInvoiceId(dto.getInvoiceId());
                invoiceService.removeByInvoiceId(dto.getInvoiceId());
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.OK;
            logger.error("removeInvoiceByItem: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
        }

        return new ResponseEntity<>(result, httpStatus);
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

    private String generateQrForPayment(long totalAmount, String content) {
        String result = "";
        String cai = EnvironmentUtil.getCAIRecharge();
        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
        vietQRGenerateDTO.setCaiValue(cai);
        String bankAccount = EnvironmentUtil.getBankAccountRecharge();
        vietQRGenerateDTO.setBankAccount(bankAccount);
        vietQRGenerateDTO.setAmount(totalAmount + "");
        vietQRGenerateDTO.setContent(content);
        result = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
        return result;
    }

    private String getSortedListString(List<String> items) {
        String result = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Collections.sort(items);
            result = mapper.writeValueAsString(items);
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getSortedListString: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private MerchantBankMapperDTO getMerchantBankMapperDTO(String data) {
        MerchantBankMapperDTO result = new MerchantBankMapperDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(data, MerchantBankMapperDTO.class);
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getMerchantBankMapperDTO: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new MerchantBankMapperDTO();
        }
        return result;
    }

    private List<BankReceivePaymentRequestDTO> getListPaymentRequest(String bankIdRecharge) {
        List<BankReceivePaymentRequestDTO> result = new ArrayList<>();
        BankReceivePaymentRequestDTO dto1 = new BankReceivePaymentRequestDTO(
                EnvironmentUtil.getBankIdRecharge(),
                EnvironmentUtil.getBankAccountRecharge(),
                EnvironmentUtil.getBankShortNameRecharge(),
                EnvironmentUtil.getUserBankNameRecharge()
        );
        if (!StringUtil.isNullOrEmpty(bankIdRecharge)) {
            dto1.setIsChecked(dto1.getBankId().equals(bankIdRecharge));
        }
        result.add(dto1);
        BankReceivePaymentRequestDTO dto2 = new BankReceivePaymentRequestDTO(
                EnvironmentUtil.getBankIdRecharge2(),
                EnvironmentUtil.getBankAccountRecharge2(),
                EnvironmentUtil.getBankShortNameRecharge(),
                EnvironmentUtil.getUserBankNameRecharge2()
        );
        if (!StringUtil.isNullOrEmpty(bankIdRecharge)) {
            dto2.setIsChecked(dto2.getBankId().equals(bankIdRecharge));
        }
        result.add(dto2);
        return result;
    }

    @GetMapping("/invoice/detail-new/{invoiceId}")
    public ResponseEntity<Object> getInvoiceByUsers(@PathVariable String invoiceId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceDetailAdminDTO dto = new InvoiceDetailAdminDTO();
            IInvoiceDTO invoiceDTO = invoiceService.getInvoiceByInvoiceDetail(invoiceId);
            if (invoiceDTO != null) {
                dto.setInvoiceId(invoiceDTO.getInvoiceId());
                dto.setInvoiceName(invoiceDTO.getInvoiceName());
                dto.setInvoiceDescription(invoiceDTO.getInvoiceDescription() != null
                        ? invoiceDTO.getInvoiceDescription() : "");
                dto.setVat(invoiceDTO.getVat());
                dto.setVatAmount(invoiceDTO.getVatAmount());
                dto.setTotalAmount(invoiceDTO.getTotalAmount());
                dto.setTotalAmountAfterVat(invoiceDTO.getTotalAmountAfterVat());

                // Tính toán tổng số tiền đã thanh toán và chưa thanh toán
                List<IInvoiceItemDetailDTO> iInvoiceItemDetailDTOS = invoiceItemService
                        .getInvoiceItemsByInvoiceId(invoiceId);
                if (iInvoiceItemDetailDTOS == null || iInvoiceItemDetailDTOS.isEmpty()) {
                    logger.error("InvoiceController: ERROR: No items found for invoice: " + invoiceId);
                    result = new ResponseMessageDTO("FAILED", "No items found for invoice");
                    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                }

                List<InvoiceItemDetailDTO> invoiceItemDetailDTOS =
                        iInvoiceItemDetailDTOS.stream().map(item -> {
                            InvoiceItemDetailDTO invoiceItemDetailDTO = new InvoiceItemDetailDTO();
                            invoiceItemDetailDTO.setInvoiceItemId(item.getInvoiceItemId());
                            invoiceItemDetailDTO.setInvoiceItemName(item.getInvoiceItemName());
                            invoiceItemDetailDTO.setUnit(item.getUnit() != null ? item.getUnit() : "");
                            invoiceItemDetailDTO.setQuantity(item.getQuantity());
                            invoiceItemDetailDTO.setAmount(item.getAmount());
                            invoiceItemDetailDTO.setTotalAmount(item.getTotalAmount());
                            invoiceItemDetailDTO.setVat(item.getVat() != null ? item.getVat() : invoiceDTO.getVat());
                            invoiceItemDetailDTO.setVatAmount(item.getVatAmount() != null ? item.getVatAmount() :
                                    Math.round(item.getTotalAmount() * invoiceDTO.getVat() / 100));
                            invoiceItemDetailDTO.setTotalAmountAfterVat(item.getAmountAfterVat() != null ?
                                    item.getAmountAfterVat() : Math.round(item.getTotalAmount() * (1 +
                                    invoiceDTO.getVat() / 100)));
                            invoiceItemDetailDTO.setStatus(item.getStatus()); // Trạng thái thanh toán của mục hóa đơn
                            return invoiceItemDetailDTO;
                        }).collect(Collectors.toList());

                dto.setInvoiceItemDetailDTOS(invoiceItemDetailDTOS);

                long totalPaid = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 1)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();
                long totalUnpaid = invoiceItemDetailDTOS.stream()
                        .filter(item -> item.getStatus() == 0)
                        .mapToLong(InvoiceItemDetailDTO::getTotalAmountAfterVat)
                        .sum();

                dto.setTotalPaid(totalPaid);
                dto.setTotalUnpaid(totalUnpaid);

                // Cập nhật trạng thái của hóa đơn
                if (totalPaid < invoiceDTO.getTotalAmountAfterVat()) {
                    dto.setStatus(3); // Chưa thanh toán hết
                } else {
                    dto.setStatus(1); // Đã thanh toán hết
                }

                // Lấy thông tin khách hàng
                List<ICustomerDetailDTO> iCustomerDetailDTOS = new ArrayList<>();
                if (StringUtil.isNullOrEmpty(invoiceDTO.getMerchantId())) {
                    iCustomerDetailDTOS = accountBankReceiveService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                } else {
                    iCustomerDetailDTOS = bankReceiveFeePackageService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                }
                if (iCustomerDetailDTOS == null || iCustomerDetailDTOS.isEmpty()) {
                    logger.error("InvoiceController: ERROR: No customer details found for invoice: " + invoiceId);
                    result = new ResponseMessageDTO("FAILED", "No customer details found for invoice");
                    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                }

                List<CustomerDetailDTO> customerDetailDTOList =
                        iCustomerDetailDTOS.stream().map(item -> {
                            CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
                            customerDetailDTO.setBankAccount(StringUtil.removeMarkString(item.getBankAccount()));
                            customerDetailDTO.setEmail(item.getEmail());
                            customerDetailDTO.setPlatform(item.getPlatform());
                            customerDetailDTO.setVso(item.getVso());
                            customerDetailDTO.setMerchantName(item.getMerchantName());
                            if (item.getMmsActive()) {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                            } else {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                            }
                            customerDetailDTO.setBankShortName(StringUtil.removeMarkString(item.getBankShortName()));
                            customerDetailDTO.setUserBankName(StringUtil.removeMarkString(item.getUserBankName()));
                            customerDetailDTO.setPhoneNo(item.getPhoneNo());
                            return customerDetailDTO;
                        }).collect(Collectors.toList());

                dto.setCustomerDetailDTOS(customerDetailDTOList);

                // Lấy thông tin gói phí
                List<IFeePackageDetailDTO> iFeePackageDetailDTOS = bankReceiveFeePackageService.getFeePackageDetail(invoiceDTO.getBankId());
                if (iFeePackageDetailDTOS == null || iFeePackageDetailDTOS.isEmpty()) {
                    logger.error("InvoiceController: ERROR: No fee package details found for invoice: " + invoiceId);
                    result = new ResponseMessageDTO("FAILED", "No fee package details found for invoice");
                    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                }

                List<FeePackageDetailDTO> feePackageDetailDTOS =
                        iFeePackageDetailDTOS.stream().map(item -> {
                            FeePackageDetailDTO feePackageDetailDTO = new FeePackageDetailDTO();
                            feePackageDetailDTO.setFeePackage(item.getFeePackage());
                            feePackageDetailDTO.setAnnualFee(item.getAnnualFee());
                            feePackageDetailDTO.setFixFee(item.getFixFee());
                            feePackageDetailDTO.setRecordType(item.getRecordType());
                            feePackageDetailDTO.setPercentFee(item.getPercentFee());
                            feePackageDetailDTO.setVat(item.getVat());
                            return feePackageDetailDTO;
                        }).collect(Collectors.toList());

                dto.setFeePackageDetailDTOS(feePackageDetailDTOS);
            } else {
                logger.error("InvoiceController: ERROR: Invoice not found: " + invoiceId);
                result = new ResponseMessageDTO("FAILED", "Invoice not found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(result, httpStatus);
            }
            result = dto;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getInvoiceByUser: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("/invoice/{invoiceId}/partial-payment")
    public ResponseEntity<Object> partialPayment(
            @PathVariable String invoiceId,
            @Valid @RequestBody PartialPaymentRequestDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceEntity invoiceEntity = invoiceService.findInvoiceById(invoiceId);
            if (invoiceEntity == null) {
                result = new ResponseMessageDTO("FAILED", "Invoice not found");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                long totalAmount = 0;
                long totalVatAmount = 0;
                long totalAmountAfterVat = 0;
                List<InvoiceItemEntity> itemsToPay = new ArrayList<>();

                for (String itemId : dto.getItemIds()) {
                    InvoiceItemEntity item = invoiceItemService.getInvoiceItemById(itemId);
                    if (item != null && item.getStatus() == 0) { // Chỉ chọn các item chưa thanh toán
                        totalAmount += item.getAmount();
                        totalVatAmount += item.getVatAmount();
                        totalAmountAfterVat += item.getTotalAfterVat();
                        itemsToPay.add(item);
                    }
                }

                if (itemsToPay.isEmpty()) {
                    result = new ResponseMessageDTO("FAILED", "No valid items to pay");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    LocalDateTime current = LocalDateTime.now();
                    long time = current.toEpochSecond(ZoneOffset.UTC);

                    // Generate unique identifiers and content for the transaction
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumberVQR = "VTS" + RandomCodeUtil.generateRandomId(10);
                    String otpPayment = RandomCodeUtil.generateOTP(6);
                    String content = traceId + " " + billNumberVQR;


                    // Create TransactionWalletEntity (credit)
                    TransactionWalletEntity walletEntityCredit = new TransactionWalletEntity();
                    UUID transWalletCreditUUID = UUID.randomUUID();
                    walletEntityCredit.setId(transWalletCreditUUID.toString());
                    walletEntityCredit.setAmount(totalAmountAfterVat + "");
                    walletEntityCredit.setBillNumber(billNumberVQR);
                    walletEntityCredit.setContent(content);
                    walletEntityCredit.setStatus(0);
                    walletEntityCredit.setTimeCreated(time);
                    walletEntityCredit.setTimePaid(0);
                    walletEntityCredit.setTransType("C");
                    walletEntityCredit.setUserId(dto.getUserId());
                    walletEntityCredit.setOtp("");
                    walletEntityCredit.setPaymentType(0);
                    walletEntityCredit.setPaymentMethod(1);
                    walletEntityCredit.setReferenceNumber(3 + "*" + dto.getUserId() + "*" + otpPayment + "*" + dto.getBankId());
                    walletEntityCredit.setPhoneNoRC("");
                    walletEntityCredit.setData(dto.getBankId());
                    walletEntityCredit.setRefId("");

                    // Create TransactionWalletEntity (debit)
                    TransactionWalletEntity walletEntityDebit = new TransactionWalletEntity();
                    UUID transWalletUUID = UUID.randomUUID();
                    walletEntityDebit.setId(transWalletUUID.toString());
                    walletEntityDebit.setAmount(totalAmountAfterVat + "");
                    walletEntityDebit.setBillNumber(billNumberVQR);
                    walletEntityDebit.setContent(content);
                    walletEntityDebit.setStatus(0);
                    walletEntityDebit.setTimeCreated(time);
                    walletEntityDebit.setTimePaid(0);
                    walletEntityDebit.setTransType("D");
                    walletEntityDebit.setUserId(dto.getUserId());
                    walletEntityDebit.setOtp(otpPayment);
                    walletEntityDebit.setPaymentType(3);
                    walletEntityDebit.setPaymentMethod(0);
                    walletEntityDebit.setReferenceNumber("");
                    walletEntityDebit.setPhoneNoRC("");
                    walletEntityDebit.setData(dto.getBankId());
                    walletEntityDebit.setRefId(transWalletCreditUUID.toString());

                    // Create TransactionReceiveEntity
                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                    String bankId = EnvironmentUtil.getBankIdRecharge();
                    String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
                    String cai = EnvironmentUtil.getCAIRecharge();
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(cai);
                    vietQRGenerateDTO.setBankAccount(bankAccount);
                    vietQRGenerateDTO.setAmount(totalAmountAfterVat + "");
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

                    UUID transReceiveUUID = UUID.randomUUID();
                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                    transactionReceiveEntity.setAmount(totalAmountAfterVat);
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
                    transactionReceiveEntity.setTerminalCode(EnvironmentUtil.getVietQrActiveKey());
                    transactionReceiveEntity.setUserId(userIdHost);
                    transactionReceiveEntity.setNote("");
                    transactionReceiveEntity.setTransStatus(0);
                    transactionReceiveEntity.setQrCode(qr);
                    transactionReceiveEntity.setUrlLink("");

                    // Create InvoiceTransactionEntity
                    InvoiceTransactionEntity invoiceTransactionEntity = new InvoiceTransactionEntity();
                    invoiceTransactionEntity.setId(UUID.randomUUID().toString());
                    invoiceTransactionEntity.setInvoiceId(invoiceEntity.getId());
                    invoiceTransactionEntity.setInvoiceItemIds(mapper.writeValueAsString(itemsToPay.stream().map(InvoiceItemEntity::getId).collect(Collectors.toList())));
                    invoiceTransactionEntity.setBankIdRecharge(dto.getBankId());
                    invoiceTransactionEntity.setBankId(dto.getBankId());
                    invoiceTransactionEntity.setUserId(dto.getUserId());
                    invoiceTransactionEntity.setQrCode(qr);
                    invoiceTransactionEntity.setInvoiceNumber(billNumberVQR);
                    invoiceTransactionEntity.setTotalAmount(totalAmountAfterVat);
                    invoiceTransactionEntity.setAmount(totalAmount);
                    invoiceTransactionEntity.setVatAmount(totalVatAmount);
                    invoiceTransactionEntity.setVat(totalVatAmount);
                    invoiceTransactionEntity.setRefId(invoiceEntity.getRefId());

                    invoiceTransactionEntity.setStatus(0);


                    // Save entities
                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                    transactionWalletService.insertTransactionWallet(walletEntityCredit);
                    transactionWalletService.insertTransactionWallet(walletEntityDebit);
                    invoiceTransactionService.insert(invoiceTransactionEntity);

                    result = new ResponseMessageDTO("SUCCESS", qr);
                    httpStatus = HttpStatus.OK;
                }
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list invoice unpaid
    @GetMapping("invoice/unpaid")
    public ResponseEntity<Object> getInvoiceUnpaid(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String time,
            @RequestParam String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        AdminExtraInvoiceDTO extraInvoiceDTO = new AdminExtraInvoiceDTO();
        DataDTO dataDTO = new DataDTO(extraInvoiceDTO);
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<AdminInvoiceDTO> data = new ArrayList<>();
            List<IAdminInvoiceDTO> dtos = new ArrayList<>();
            IAdminExtraInvoiceDTO extraInvoiceDTO1 = null;

            switch (type) {
                case 1:
                    dtos = invoiceService.getInvoiceUnpaid(value, offset, size, time, userId);
                    totalElement = invoiceService.countInvoiceUnpaid(value, time, userId);
                    break;
                default:
                    dtos = new ArrayList<>();
                    break;
            }

            extraInvoiceDTO1 = invoiceService.getExtraInvoice(time);
            if (extraInvoiceDTO1 != null) {
                extraInvoiceDTO = new AdminExtraInvoiceDTO();
                extraInvoiceDTO.setMonth(time);
                extraInvoiceDTO.setCompleteCount(extraInvoiceDTO1.getCompleteCount());
                extraInvoiceDTO.setCompleteAmount(extraInvoiceDTO1.getCompleteFee());
                extraInvoiceDTO.setPendingCount(extraInvoiceDTO1.getPendingCount());
                extraInvoiceDTO.setPendingAmount(extraInvoiceDTO1.getPendingFee());
            }

            data = dtos.stream().map(item -> {
                AdminInvoiceDTO dto = new AdminInvoiceDTO();
                AccountBankInfoDTO bankInfoDTO = getBankAccountInfoByData(item.getData());
                String qrCode = "";
                dto.setInvoiceId(item.getInvoiceId());
                dto.setTimePaid(item.getTimePaid());
                dto.setVso(item.getVso() != null ? item.getVso() : "");
                dto.setMidName(item.getMidName() != null ? item.getMidName() : "");
                dto.setAmount(item.getAmount());
                dto.setBankShortName(bankInfoDTO.getBankShortName());
                dto.setBankAccount(bankInfoDTO.getBankAccount());
                dto.setQrCode(qrCode);
                dto.setVat(item.getVat());
                dto.setVatAmount(item.getVatAmount());
                dto.setAmountNoVat(item.getAmountNoVat());
                dto.setBillNumber(item.getBillNumber());
                dto.setInvoiceName(item.getInvoiceName());
                dto.setFullName(bankInfoDTO.getUserBankName());
                dto.setPhoneNo(item.getPhoneNo());
                dto.setEmail(item.getEmail() != null ? item.getEmail() : "");
                dto.setTimeCreated(item.getTimeCreated());
                dto.setStatus(item.getStatus());


                // push notification
                // --call api topup
                String requestId = VNPTEpayUtil
                        .createRequestID(EnvironmentUtil
                                .getVnptEpayPartnerName());

                // --initial notification data
                LocalDateTime localTimeM = LocalDateTime
                        .now();
                long timeM = localTimeM
                        .toEpochSecond(ZoneOffset.UTC);
                String msgErrorCode = "Error push notification invoice";
                UUID notificationUUID = UUID.randomUUID();
                String notiType = NotificationUtil.getNotiMobileTopup();
                String title = NotificationUtil.getNotiTitleInvoiceUnpaid();
                String message = "Bạn có hoá đơn "
                        + dto.getBillNumber()
                        + " chưa thanh toán. Vui Lòng kiểm tra lại trên hệ thống VietQR VN.";

                NotificationEntity notiEntity = new NotificationEntity();
                notiEntity.setId(notificationUUID.toString());
                notiEntity.setRead(false);
                notiEntity.setMessage(message);
                notiEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                notiEntity.setType(notiType);
                notiEntity.setUserId(userId);
                notiEntity.setData(userId); // cần lấy userID ở invoicce
                Map<String, String> datas = new HashMap<>();
                datas.put("notificationType", notiType); // thuộc loại noti nào
                datas.put("notificationId", notificationUUID.toString()); // id của bảng notification
                datas.put("html", dto.getInvoiceName() + ""); // truyền html (any)
                datas.put("invoiceId", dto.getInvoiceId());  //invoice ID
                //-- có thể truyền user ID
                datas.put("time", time + "");
                datas.put("phoneNo", dto.getPhoneNo()); // phone number của user
                //datas.put("billNumber", tranMobileEntity.getBillNumber());  // bill number
                //datas.put("paymentMethod", "1");
                //datas.put("paymentType", "1");
                datas.put("status", 1 + ""); // truyền Status của Invoice (1 or 0)
                datas.put("messageError", msgErrorCode);

                pushNotification(title, message, notiEntity, datas, notiEntity.getUserId());
                //

                //push form noti
                //initialize datas check
                int typeCheck = 0;
                String statusCheck = datas.put("status", "1");
                String bankCodeCheck = datas.put("bankCode", "MB");
                String terminalCodeCheck = datas.put("terminalCode", "");
                String terminalNameCheck = datas.put("terminalName", "");
                String getTransType = "C";

                // check conditions to push form notifications
                if (dto.getStatus() == 0) {
                    datas.put("html", "Bạn có hoá đơn " + dto.getAmount() + " cần thanh toán.");
                } // thông báo hoá đơn chưa thanh toán

                if (getTransType == "C" && typeCheck == 2) {
                    datas.put("html", "+" + dto.getAmount() + " VNĐ đến "
                            + bankCodeCheck + " - " + dto.getBankAccount());
                } // Nhận tiền Đến

                if (getTransType == "C" && typeCheck == 1) {
                    datas.put("html", "+ " + dto.getAmount() + " VNĐ đến MB Bank - "
                            + dto.getBankAccount() + " - "
                            + dto.getInvoiceName()
                            + " - " + terminalCodeCheck);
                } // Cập nhật tài khoản cửa hàng

                if (getTransType == "D") {
                    datas.put("html", "+" + dto.getAmount() + " VNĐ từ "
                            + dto.getBankShortName() + " - " + dto.getBankAccount());
                } // Chuyển tiền đi

                if (getTransType == "C" && typeCheck == 0) {
                    datas.put("html", dto.getBankAccount() + "Chưa biết trả gì");
                }
                //--

                return dto;
            }).collect(Collectors.toList());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageDTO.setTotalElement(totalElement);
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageResponseDTO.setMetadata(pageDTO);
            dataDTO.setExtraData(extraInvoiceDTO);
            dataDTO.setItems(data);
            pageResponseDTO.setData(dataDTO);
            httpStatus = HttpStatus.OK;
            result = pageResponseDTO;


        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
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
                            + e.toString());
        }
    }
}
