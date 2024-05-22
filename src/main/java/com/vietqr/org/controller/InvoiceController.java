package com.vietqr.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    MerchantSyncService merchantSyncService;

    @Autowired
    BankReceiveFeePackageService bankReceiveFeePackageService;

    @Autowired
    TrMonthService trMonthService;

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
                    dto.setMerchantId(item.getMerchantId());
                    dto.setUserBankName(item.getUserBankName());
                    dto.setBankShortName(bankShortNameMap.getOrDefault(item.getBankTypeId(), ""));
                    dto.setPhoneNo(item.getPhoneNo());
                    dto.setEmail(StringUtil.getValueNullChecker(item.getEmail()));
                    dto.setBankAccount(item.getBankAccount());
                    dto.setFeePackage(item.getFeePackage());
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
                    dto.setFeePackage(item.getFeePackage());
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
            extraInvoiceDTO1 = invoiceService.getExtraInvoice(time);
            if (extraInvoiceDTO1 != null) {
                extraInvoiceDTO = new AdminExtraInvoiceDTO();
                extraInvoiceDTO.setMonth(time);
                extraInvoiceDTO.setCompleteCount(extraInvoiceDTO1.getCompleteCount());
                extraInvoiceDTO.setCompleteFee(extraInvoiceDTO1.getCompleteFee());
                extraInvoiceDTO.setPendingCount(extraInvoiceDTO1.getPendingCount());
                extraInvoiceDTO.setPendingFee(extraInvoiceDTO1.getPendingFee());
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
                data.setMerchantId(merchantId);
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
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
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
                AccountBankInfoDTO bankAccountInfoDTO = getBankAccountInfoByData(dto.getData());
                data.setQrCode(generateQrForPayment(dto.getTotalAmountAfterVat(), dto.getContent()));
                data.setTotalAmountAfterVat(dto.getTotalAmountAfterVat());
                data.setInvoiceName(dto.getInvoiceName());
                data.setBankAccount(bankAccountInfoDTO.getBankAccount());
                data.setBankShortName(bankAccountInfoDTO.getBankShortName());
                data.setInvoiceNumber(dto.getInvoiceNumber());
                data.setUserBankName(bankAccountInfoDTO.getUserBankName());
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
            logger.error("InvoiceController: ERROR: getMerchantList: " + e.getMessage()
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
            entity.setId(invoiceId.toString());
            entity.setName(dto.getInvoiceName());
            entity.setDescription(dto.getDescription());
            entity.setTimePaid(0);
            entity.setStatus(0);
            entity.setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : "");
            entity.setBankId(dto.getBankId() != null ? dto.getBankId() : "");
            IMerchantBankMapperDTO merchantMapper;
            if (StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                merchantMapper = accountBankReceiveService
                        .getMerchantBankMapper(dto.getBankId());
            } else {
                merchantMapper = bankReceiveConnectionService
                        .getMerchantBankMapper(dto.getMerchantId(), dto.getBankId());
            }
            MerchantBankMapperDTO merchantBankMapperDTO = new MerchantBankMapperDTO();
            if (merchantMapper != null) {
                merchantBankMapperDTO.setUserBankName(merchantMapper.getUserBankName());
                merchantBankMapperDTO.setMerchantName(merchantMapper.getMerchantName());
                merchantBankMapperDTO.setVso(merchantMapper.getVso());
                merchantBankMapperDTO.setEmail(merchantMapper.getEmail());
                merchantBankMapperDTO.setBankAccount(merchantMapper.getBankAccount());
                merchantBankMapperDTO.setBankShortName(merchantMapper.getBankShortName());
                merchantBankMapperDTO.setPhoneNo(merchantBankMapperDTO.getPhoneNo());

                entity.setUserId(merchantMapper.getUserId());
            }
            try {
                entity.setData(mapper.writeValueAsString(merchantBankMapperDTO));
                entity.setDataType(1);
            } catch (Exception ignored) {
                entity.setData("");
                entity.setDataType(9);
            }
            List<InvoiceItemEntity> invoiceItemEntities = new ArrayList<>();
            for (InvoiceItemCreateDTO item : dto.getItems()) {

                InvoiceItemEntity invoiceItemEntity
                        = invoiceItemService.getInvoiceItemById(item.getItemId());
                invoiceItemEntity.setId(item.getItemId());
                invoiceItemEntity.setInvoiceId(invoiceId.toString());
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

            entity.setRefId("");
            String userId = accountBankReceiveService.getUserIdByBankId(dto.getBankId());
            if (userId != null && !userId.isEmpty()) {
                entity.setUserId(userId);
            } else {
                entity.setUserId("");
            }
            invoiceItemService.removeByInvoiceId(invoiceId);
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
                    customerDTO.setMerchantId(merchantEditDetail.getMerchantId());
                    customerDTO.setMerchantName(merchantEditDetail.getMerchantName());
                } else {
                    customerDTO.setMerchantId("");
                    customerDTO.setMerchantName("");
                }

                if (dto.getBankId() != null) {
                    IBankAccountInvoiceDTO bankAccountInvoiceDTO =
                            bankReceiveFeePackageService
                                    .getBankInvoiceByBankId(dto.getBankId());
                    customerDTO.setBankId(bankAccountInvoiceDTO.getBankId());
                    customerDTO.setPhoneNo(bankAccountInvoiceDTO.getPhoneNo());
                    customerDTO.setEmail(bankAccountInvoiceDTO.getEmail());
                    customerDTO.setFeePackage(bankAccountInvoiceDTO.getFeePackage());
                    AccountBankInfoDTO bankAccountInfoDTO = getBankAccountInfoByData(dto.getData());
                    customerDTO.setBankAccount(bankAccountInfoDTO.getBankAccount());
                    customerDTO.setBankShortName(bankAccountInfoDTO.getBankShortName());
                    customerDTO.setUserBankName(bankAccountInfoDTO.getUserBankName());
                    if (bankAccountInfoDTO.getMmsActive()) {
                        customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                    } else {
                        customerDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                    }
                    customerDTO.setVat(8);
                }
                data.setUserInformation(customerDTO);

                List<IInvoiceItemDetailDTO> invoiceItemDetailDTOS = invoiceItemService.getInvoiceItemsByInvoiceId(invoiceId);
                List<InvoiceItemDetailDTO> invoiceItems
                        = invoiceItemDetailDTOS.stream().map(item -> {
                    InvoiceItemDetailDTO detailDTO = new InvoiceItemDetailDTO();
                    detailDTO.setInvoiceItemId(item.getInvoiceItemId());
                    detailDTO.setInvoiceItemName(item.getInvoiceItemName());
                    detailDTO.setUnit(item.getUnit());
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

    @GetMapping("invoice/detail/{invoiceId}")
    public ResponseEntity<Object> getInvoiceByUser(
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
                            return invoiceItemDetailDTO;
                        }).collect(Collectors.toList());

                dto.setInvoiceItemDetailDTOS(invoiceItemDetailDTOS);

                List<ICustomerDetailDTO> iCustomerDetailDTOS = new ArrayList<>();
                if (StringUtil.isNullOrEmpty(invoiceDTO.getMerchantId())) {
                    iCustomerDetailDTOS =
                            accountBankReceiveService.getCustomerDetailByBankId(invoiceDTO.getBankId());
                } else {
                    iCustomerDetailDTOS = new ArrayList<>();
                }
                List<CustomerDetailDTO> customerDetailDTOList =
                        iCustomerDetailDTOS.stream().map(item -> {
                            CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
                            customerDetailDTO.setBankAccount(item.getBankAccount());
                            customerDetailDTO.setEmail(item.getEmail());
                            customerDetailDTO.setPlatform(item.getPlatform());
                            customerDetailDTO.setVso(item.getVso());
                            customerDetailDTO.setMerchantName(item.getMerchantName());
                            if (item.getMmsActive()) {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrProPackage());
                            } else {
                                customerDetailDTO.setConnectionType(EnvironmentUtil.getVietQrPlusPackage());
                            }
                            customerDetailDTO.setBankShortName(item.getBankShortName());
                            customerDetailDTO.setUserBankName(item.getUserBankName());
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
            logger.error("InvoiceController: ERROR: getMerchantList: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice/{userId}")
    public ResponseEntity<Object> getInvoiceByUser(
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
            if (bankId == null || bankId.isEmpty()) {
                if (time == null || time.isEmpty()) {
                    dtos = invoiceService.getInvoiceByUserId(userId, status, offset, size);
                    totalElement = invoiceService.countInvoiceByUserId(userId, status);
                } else {
                    dtos = invoiceService.getInvoiceByUserIdAndMonth(userId, status, time, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndMonth(userId, status, time);
                }
            } else {
                if (time == null || time.isEmpty()) {
                    dtos = invoiceService.getInvoiceByUserIdAndBankId(userId, status, bankId, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndBankId(userId, status, bankId);
                } else {
                    dtos = invoiceService.getInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, time, offset, size);
                    totalElement = invoiceService.countInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, time);
                }
            }
            data = dtos.stream().map(item -> {
                InvoiceResponseDTO dto = new InvoiceResponseDTO();
                dto.setInvoiceId(item.getInvoiceId());
                dto.setInvoiceName(item.getInvoiceName());
                dto.setBillNumber(item.getBillNumber());
                dto.setInvoiceNumber(item.getInvoiceNumber());
                dto.setInvoiceName(item.getInvoiceName());
                dto.setTimeCreated(item.getTimeCreated());
                dto.setTimePaid(item.getTimePaid());
                dto.setStatus(item.getStatus());
                dto.setBankId(item.getBankId());
                try {
                    BankAccountInfoDTO bankAccountInfoDTO = mapper.readValue(item.getData(), BankAccountInfoDTO.class);
                    if (bankAccountInfoDTO != null) {
                        dto.setBankAccount(bankAccountInfoDTO.getBankAccount() != null ?
                                bankAccountInfoDTO.getBankAccount() : "");
                        dto.setBankShortName(bankAccountInfoDTO.getBankShortName() != null ?
                                bankAccountInfoDTO.getBankShortName() : "");
                    }
                } catch (JsonProcessingException e) {
                    dto.setBankAccount("");
                    dto.setBankShortName("");
                }
                String cai = EnvironmentUtil.getCAIRecharge();
                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                vietQRGenerateDTO.setCaiValue(cai);
                String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                vietQRGenerateDTO.setBankAccount(bankAccount);
                vietQRGenerateDTO.setAmount(item.getTotalAmount() + "");
                vietQRGenerateDTO.setContent(item.getContent());
                String bankCode = EnvironmentUtil.getBankCodeRecharge();
                String bankName = EnvironmentUtil.getBankNameRecharge();
                String userBankName = EnvironmentUtil.getUserBankNameRecharge();
                vietQRGenerateDTO.setBankAccount(bankAccount);
                vietQRGenerateDTO.setAmount(dto.getTotalAmount() + "");
                dto.setBankAccountForPayment(bankAccount);
                dto.setUserBankNameForPayment(userBankName);
                dto.setBankNameForPayment(bankName);
                dto.setBankCodeForPayment(bankCode);
                String qrCode = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                dto.setQrCode(qrCode);
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
            logger.error("InvoiceController: ERROR: getInvoiceByUser: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/invoice-detail/{invoiceId}")
    public ResponseEntity<Object> getInvoiceByItem(
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
            response.setBillNumber(dto.getBillNumber());
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
            logger.error("InvoiceController: ERROR: getInvoiceByUser: " + e.getMessage()
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
                    data = new InvoiceCreateItemDTO();
                    data.setItemId(monthYear + type);
                    data.setVat(vat);
                    data.setTime(time);
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
                // phi giao dich
                case 1:
                    data = new InvoiceCreateItemDTO();
                    data.setItemId(monthYear + type);
                    data.setVat(vat);
                    data.setTime(time);
                    data.setType(type);
                    data.setContent(EnvironmentUtil.getVietQrNameTransFee() + monthYear);
                    data.setUnit(EnvironmentUtil.getMonthUnitNameVn());
                    data.setQuantity(1);
                    feePackage = bankReceiveFeePackageService.getFeePackageByBankId(bankId);
                    String timeFormat = DateTimeUtil.removeFormatTime(time);
                    TrMonthDTO dto = trMonthService.getTrMonthByMonth(timeFormat);
                    if (dto != null) {
                        BrStatisticDTO brStatisticDTO = getBrStatisticByBankId(bankId, dto.getData());
                        if (feePackage != null) {
                            long totalAmount = getTotalAmount(feePackage, brStatisticDTO);
                            data.setAmount(totalAmount);
                            data.setTotalAmount(totalAmount);
                            data.setVatAmount(Math.round(feePackage.getVat() / 100 * totalAmount));
                            data.setAmountAfterVat(Math.round((feePackage.getVat() + 1) / 100 * totalAmount));
                        }
                    }
                    break;
                // phi khac
                case 9:
                    data = new InvoiceCreateItemDTO();
                    data.setItemId(monthYear + type);
                    data.setVat(vat);
                    data.setTime(time);
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
            entity.setTimeCreated(time);
            entity.setTimePaid(0);
            entity.setStatus(0);
            entity.setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : "");
            entity.setBankId(dto.getBankId() != null ? dto.getBankId() : "");
            IMerchantBankMapperDTO merchantMapper;
            String userId = "";
            if (StringUtil.isNullOrEmpty(dto.getMerchantId())) {
                merchantMapper = accountBankReceiveService
                        .getMerchantBankMapper(dto.getBankId());
            } else {
                merchantMapper = bankReceiveConnectionService
                        .getMerchantBankMapper(dto.getMerchantId(), dto.getBankId());
            }
            MerchantBankMapperDTO merchantBankMapperDTO = new MerchantBankMapperDTO();
            if (merchantMapper != null) {
                merchantBankMapperDTO.setUserBankName(merchantMapper.getUserBankName());
                merchantBankMapperDTO.setMerchantName(merchantMapper.getMerchantName());
                merchantBankMapperDTO.setVso(merchantMapper.getVso());
                merchantBankMapperDTO.setEmail(merchantMapper.getEmail());
                merchantBankMapperDTO.setBankAccount(merchantMapper.getBankAccount());
                merchantBankMapperDTO.setBankShortName(merchantMapper.getBankShortName());
                merchantBankMapperDTO.setPhoneNo(merchantBankMapperDTO.getPhoneNo());

                userId = merchantMapper.getUserId();
                entity.setUserId(merchantMapper.getUserId());
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
            for (InvoiceItemCreateDTO item : dto.getItems()) {

                InvoiceItemEntity invoiceItemEntity = new InvoiceItemEntity();
                invoiceItemEntity.setId(UUID.randomUUID().toString());
                invoiceItemEntity.setInvoiceId(invoiceId.toString());
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
                        invoiceItemEntity.setTypeName(EnvironmentUtil.getVietQrNameTransFee());
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

            // create transaction_wallet_debit
            TransactionWalletEntity walletEntityDebit = new TransactionWalletEntity();
            UUID transWalletUUID = UUID.randomUUID();
            walletEntityDebit.setId(transWalletUUID.toString());
            walletEntityDebit.setAmount(totalAmountAfterVat + "");
            walletEntityDebit.setBillNumber(invoiceNumber);
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

            // generate VQR
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

            // insert transaction_receive
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

            entity.setRefId(transWalletUUID.toString());

            transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
            transactionWalletService.insertAll(transactionWalletEntities);
            invoiceItemService.insertAll(invoiceItemEntities);
            invoiceService.insert(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.OK;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("invoice/update")
    public ResponseEntity<Object> updateInvoiceByItem(
            @Valid @RequestBody InvoiceUpdateDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long vatAmount = 0;
            long totalAmount = 0;
            long totalAmountAfterVat = 0;
            InvoiceUpdateItemDTO invoiceDTO = invoiceService.getInvoiceById(dto.getInvoiceId());
            InvoiceItemEntity invoiceItemEntity = invoiceItemService.getInvoiceItemById(dto.getItemId());
            vatAmount = invoiceDTO.getVatAmount() + dto.getVatAmount() - invoiceItemEntity.getVatAmount();
            totalAmount = invoiceDTO.getTotalAmount() + dto.getTotalAmount() - invoiceItemEntity.getTotalAmount();
            totalAmountAfterVat = invoiceDTO.getTotalAmountAfterVat() + dto.getAmountAfterVat()
                    - invoiceItemEntity.getTotalAfterVat();
            invoiceItemEntity.setAmount(dto.getAmount());
            invoiceItemEntity.setQuantity(dto.getQuantity());
            invoiceItemEntity.setTotalAmount(dto.getAmount() * dto.getQuantity());
            invoiceItemEntity.setTotalAfterVat(dto.getAmountAfterVat());
            invoiceItemEntity.setName(dto.getContent());
            invoiceItemEntity.setDescription(dto.getContent());
            switch (dto.getType()) {
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
            invoiceItemEntity.setUnit(dto.getUnit());
            invoiceItemEntity.setVat(dto.getVat());
            invoiceItemEntity.setVatAmount(dto.getVatAmount());
            invoiceItemEntity.setDataType(1);

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
//            transactionReceiveService.upda
            //
            invoiceItemService.insert(invoiceItemEntity);
            invoiceService.updateInvoiceById(vatAmount, totalAmount,
                    totalAmountAfterVat, dto.getInvoiceId());


            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.OK;
            logger.error("updateInvoiceByItem: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
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
            invoiceItemService.removeByInvoiceId(dto.getInvoiceId());
            invoiceService.removeByInvoiceId(dto.getInvoiceId());
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

    @PostMapping("invoice-item/remove")
    public ResponseEntity<Object> removeInvoiceByItem(
            @Valid @RequestBody InvoiceItemRemoveDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long vatAmount = 0;
            long totalAmount = 0;
            long totalAmountAfterVat = 0;
            InvoiceUpdateItemDTO entity = invoiceService.getInvoiceById(dto.getInvoiceId());
            IInvoiceItemRemoveDTO invoiceItemRemoveDTO =
                    invoiceItemService.getInvoiceRemoveByInvoiceId(dto.getItemId());
            vatAmount = entity.getVatAmount() - invoiceItemRemoveDTO.getVatAmount();
            totalAmount = entity.getTotalAmount() - invoiceItemRemoveDTO.getTotalAmount();
            totalAmountAfterVat = entity.getTotalAmount() - invoiceItemRemoveDTO.getTotalAmountAfterVat();
            //update transaction wallet

            //
            invoiceItemService.removeById(dto.getItemId());
            invoiceService.updateInvoiceById(vatAmount, totalAmount,
                    totalAmountAfterVat, dto.getInvoiceId());
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

    private BrStatisticDTO getBrStatisticByBankId(String bankId, String data) {
        BrStatisticDTO brStatisticDTO = new BrStatisticDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<BrStatisticDTO> brStatisticDTOS = new ArrayList<>();
            brStatisticDTOS = mapper.readValue(data, new TypeReference<List<BrStatisticDTO>>() {
            });
            for (BrStatisticDTO dto : brStatisticDTOS) {
                if (dto.getBrId().equals(bankId)) {
                    brStatisticDTO = dto;
                }
            }
        } catch (Exception e) {
            brStatisticDTO.setBrId(bankId);
        }
        return brStatisticDTO;
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

    private long getTotalAmount(IInvoiceItemCreateDTO feePackage, BrStatisticDTO brStatisticDTO) {
        long feeCredit = 0;
        long feeCreCount = 0;
        switch (feePackage.getRecordType()) {
            case 0:
                long recon = brStatisticDTO.getRecon();
                long reCount = brStatisticDTO.getRecCount();
                feeCredit = Math
                        .round(feePackage.getPercentFee() * recon / 100);
                feeCreCount = reCount * feePackage.getFixFee();
                break;
            case 1:
                long credit = brStatisticDTO.getCredit();
                int creCount = brStatisticDTO.getCreCount();
                feeCredit = Math
                        .round(feePackage.getPercentFee() * credit / 100);
                feeCreCount = creCount * feePackage.getFixFee();
                break;
            default:
                break;
        }
        long totalAmount = Math.round(feeCredit + feeCreCount - feePackage.getAnnualFee());
        if (totalAmount < 0) totalAmount = 0;
        return totalAmount;
    }
}
