package com.vietqr.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.BankAccountInfoDTO;
import com.vietqr.org.entity.InvoiceEntity;
import com.vietqr.org.entity.InvoiceItemEntity;
import com.vietqr.org.service.InvoiceItemService;
import com.vietqr.org.service.InvoiceService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.StringUtil;
import com.vietqr.org.util.VietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @PostMapping("/invoice")
    public ResponseEntity<ResponseMessageDTO> insertInvoice(
            @Valid @RequestBody InvoiceRequestDTO dto
            ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            InvoiceEntity entity = new InvoiceEntity();
            String invoiceId = UUID.randomUUID().toString();
            entity.setId(invoiceId);
            entity.setInvoiceId(RandomCodeUtil.generateRandomCode(10));
            entity.setBankId(dto.getBankId());
            entity.setName(dto.getName());
            LocalDateTime current = LocalDateTime.now();
            long time = current.toEpochSecond(ZoneOffset.UTC);
            entity.setTimeCreated(time);
            entity.setTimePaid(0);
            entity.setStatus(2);
            entity.setMerchantId(dto.getMerchantId());
            long total = 0;
            List<InvoiceItemEntity> itemEntities = new ArrayList<>();
            for (InvoiceItemDTO item: dto.getItems()) {
                InvoiceItemEntity itemEntity = new InvoiceItemEntity();
                itemEntity.setInvoiceId(invoiceId);
                itemEntity.setId(UUID.randomUUID().toString());
                itemEntity.setType(item.getType());
                itemEntity.setTypeName(item.getTypeName());
                itemEntity.setAmount(item.getAmount());
                itemEntity.setQuantity(item.getQuantity());
                itemEntity.setTotalAmount(item.getAmount() * item.getQuantity());
                itemEntities.add(itemEntity);
            }
            invoiceService.insert(entity);
            invoiceItemService.insertAll(itemEntities);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: insertInvoice: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
//
//    @GetMapping("/invoice/check-item")
//    public ResponseEntity<ResponseMessageDTO> checkItemByMerchantAndBank(
//            @Valid @RequestBody InvoiceRequestDTO dto
//    ) {
//
//    }

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
                                bankAccountInfoDTO.getBankAccount(): "");
                        dto.setBankShortName(bankAccountInfoDTO.getBankShortName() != null ?
                                bankAccountInfoDTO.getBankShortName(): "");
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
                            bankAccountInfoDTO.getBankAccount(): "");
                    response.setBankShortName(bankAccountInfoDTO.getBankShortName() != null ?
                            bankAccountInfoDTO.getBankShortName(): "");
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

}
