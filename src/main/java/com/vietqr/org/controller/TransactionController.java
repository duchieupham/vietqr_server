package com.vietqr.org.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.apache.poi.ss.usermodel.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionController {
    private static final Logger logger = Logger.getLogger(TransactionController.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionBankService transactionBankService;

    @Autowired
    TransReceiveRequestMappingService transReceiveRequestMappingService;

    @Autowired
    MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    TransactionReceiveHistoryService transactionReceiveHistoryService;

    @Autowired
    TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    ImageService imageService;

    @Autowired
    TerminalService terminalService;

    @Autowired
    TransactionReceiveImageService transactionReceiveImageService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    MerchantMemberService merchantMemberService;

    @Autowired
    TransactionRPAService transactionRPAService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    private TransactionRefundService transactionRefundService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    TransReceiveTempService transReceiveTempService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    InvoiceService invoiceService;

    @GetMapping("admin/transactions")
    public ResponseEntity<List<TransactionReceiveAdminListDTO>> getTransactionAdmin(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            @RequestParam(value = "offset") int offset) {
        List<TransactionReceiveAdminListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type (search by)
            // - 0: bankAccount
            // - 1: reference_number (FT Code)
            // - 2: order_id
            // - 3: content
            // - 4: terminal code
            // - 9: all
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
            if (checkEmptyDate) {
                switch (type) {
                    case 0:
                        result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        result = transactionReceiveService.getTransByFtCode(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        result = transactionReceiveService.getTransByOrderId(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        result = transactionReceiveService.getTransByContent(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 4:
                        result = transactionReceiveService.getTransByTerminalCodeAllDate(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 9:
                        result = transactionReceiveService.getAllTransAllDate(offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            } else {
                switch (type) {
                    case 0:
                        result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        result = transactionReceiveService.getTransByFtCode(value, offset, fromDate, toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        result = transactionReceiveService.getTransByOrderId(value, offset, fromDate, toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        result = transactionReceiveService.getTransByContent(value, offset, fromDate, toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 4:
                        result = transactionReceiveService.getTransByTerminalCodeFromDate(value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 9:
                        result = transactionReceiveService.getAllTransFromDate(fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/transactions")
    public ResponseEntity<List<TransactionReceiveAdminListDTO>> getTransactionMerchant(
            @RequestParam(value = "merchantId") String merchantId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            @RequestParam(value = "offset") int offset) {
        List<TransactionReceiveAdminListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type (search by)
            // - 0: bankAccount
            // - 1: reference_number (FT Code)
            // - 2: order_id
            // - 3: content
            // - 9: all
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
            if (checkEmptyDate) {
                switch (type) {
                    case 0:
                        result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        result = transactionReceiveService.getTransByFtCodeAndMerchantId(value, merchantId, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        result = transactionReceiveService.getTransByOrderIdAndMerchantId(value, merchantId, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        result = transactionReceiveService.getTransByContentAndMerchantId(value, merchantId, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 4:
                        result = transactionReceiveService.getTransByTerminalCodeAndMerchantIdAllDate(value, merchantId,
                                offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 9:
                        result = transactionReceiveService.getAllTransAllDateByMerchantId(merchantId, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getTransactionMerchant: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            } else {
                switch (type) {
                    case 0:
                        result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        result = transactionReceiveService.getTransByFtCodeAndMerchantId(value, merchantId, offset, fromDate,
                                toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        result = transactionReceiveService.getTransByOrderIdAndMerchantId(value, merchantId, offset, fromDate,
                                toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        result = transactionReceiveService.getTransByContentAndMerchantId(value, merchantId, offset, fromDate,
                                toDate);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 4:
                        result = transactionReceiveService.getTransByTerminalCodeAndMerchantIdFromDate(fromDate, toDate,
                                value, merchantId, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 9:
                        result = transactionReceiveService.getAllTransFromDateByMerchantId(fromDate, toDate, merchantId,
                                offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getTransactionMerchant: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // sync
    @GetMapping("terminal/transactions")
    public ResponseEntity<List<TransactionRelatedResponseDTO>> getTransactionUser(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            @RequestParam(value = "offset") int offset) {
        List<TransactionRelatedResponseDTO> result = new ArrayList<>();
        List<TransactionRelatedDTO> dtos = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 5: status
            if (terminalCode != null) {
                if (StringUtil.removeDiacritics(terminalCode).contains("Tat ca")) {
                    terminalCode = "";
                }
            }
            List<String> listCode = new ArrayList<>();
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
            boolean checkEmptyTerminal = StringUtil.isNullOrEmpty(terminalCode);
            // old logic
//            List<String> terminalCodeAccess = accountBankReceiveShareService.checkUserExistedFromBankId(userId, bankId);

            // new logic
            // check user existed in bank
            List<String> roles = merchantMemberRoleService.getRoleByUserIdAndBankId(userId, bankId);
            // check role có được xem giao dịch chưa xaác nhân không và là cấp nào nào
            // check level:
            // 0 : terminal, 1: merchant
            // check accept see type = 2
            int acceptSee = 0;
            int level = 0;
            if (roles != null && !roles.isEmpty()) {
                if (roles.contains(EnvironmentUtil.getRequestReceiveMerchantRoleId())) {
                    level = 1;
                    acceptSee = 1;
                } else if (roles.contains(EnvironmentUtil.getRequestReceiveTerminalRoleId())) {
                    acceptSee = 1;
                } else if (roles.contains(EnvironmentUtil.getOnlyReadReceiveMerchantRoleId())) {
                    level = 1;
                    acceptSee = 1;
                }
            }
            List<String> terminalCodeAccess = new ArrayList<>();
            if (level == 0) {
                terminalCodeAccess = terminalBankReceiveService.getTerminalCodeByUserIdAndBankId(userId, bankId);
            } else {
                terminalCodeAccess = terminalBankReceiveService.getTerminalCodeByUserIdAndBankIdNoTerminal(userId, bankId);
            }
            // check terminal sub code
            if (terminalCodeAccess != null && !terminalCodeAccess.isEmpty()) {
                if (!checkEmptyTerminal) {
                    listCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                    listCode.add(terminalCode);
                } else {
                    listCode = terminalBankReceiveService.getTerminalCodeByMainTerminalCodeList(terminalCodeAccess);
                    listCode.addAll(terminalCodeAccess);
                }
                if (!listCode.isEmpty() && checkEmptyDate) {
                    switch (type) {
                        case 1:
                            dtos = transactionReceiveService
                                    .getTransTerminalByFtCode(bankId, userId, value, terminalCode, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 2:
                            dtos = transactionReceiveService
                                    .getTransTerminalByOrderId(bankId, userId, value, terminalCode, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 3:
                            value = value.replace("-", " ").trim();
                            dtos = transactionReceiveService
                                    .getTransTerminalByContent(bankId, userId, value, terminalCode, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 5:
                            dtos = transactionReceiveService
                                    .getTransTerminalByStatus(bankId, userId, Integer.parseInt(value), terminalCode, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 9:
                            dtos = transactionReceiveService
                                    .getAllTransTerminal(bankId, userId, terminalCode, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        default:
                            logger.error("getTransactionUser: ERROR: INVALID TYPE");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            break;
                    }
                } else if (!listCode.isEmpty()) {
                    switch (type) {
                        case 1:
                            if (acceptSee == 0) {
                                dtos = transactionReceiveService
                                        .getTransTerminalByFtCode(bankId, userId, value, listCode, fromDate, toDate, offset);
                            } else {
                                listCode.add("");
                                dtos = transactionReceiveService
                                        .getTransTerminalWithType2ByFtCode(bankId, userId, value, listCode, fromDate, toDate, offset);
                            }
                            httpStatus = HttpStatus.OK;
                            break;
                        case 2:
                            if (acceptSee == 0) {
                                dtos = transactionReceiveService
                                        .getTransTerminalByOrderId(bankId, userId, value, listCode, fromDate, toDate, offset);
                            } else {
                                listCode.add("");
                                dtos = transactionReceiveService
                                        .getTransTerminalWithType2ByOrderId(bankId, userId, value, listCode, fromDate, toDate, offset);
                            }
                            httpStatus = HttpStatus.OK;
                            break;
                        case 3:
                            value = value.replace("-", " ").trim();
                            if (acceptSee == 0) {
                                dtos = transactionReceiveService
                                        .getTransTerminalByContent(bankId, userId, value, listCode, fromDate, toDate, offset);
                            } else {
                                listCode.add("");
                                dtos = transactionReceiveService
                                        .getTransTerminalWithType2ByContent(bankId, userId, value, listCode, fromDate, toDate, offset);
                            }
                            httpStatus = HttpStatus.OK;
                            break;
                        case 5:
                            if (acceptSee == 0) {
                                dtos = transactionReceiveService
                                        .getTransTerminalByStatus(bankId, userId, Integer.parseInt(value), listCode, fromDate, toDate, offset);
                            } else {
                                listCode.add("");
                                dtos = transactionReceiveService
                                        .getTransTerminalWithType2ByStatus(bankId, userId, Integer.parseInt(value), listCode, fromDate, toDate, offset);
                            }
                            httpStatus = HttpStatus.OK;
                            break;
                        case 9:
                            if (acceptSee == 0) {
                                dtos = transactionReceiveService
                                        .getAllTransTerminal(bankId, userId, listCode, fromDate, toDate, offset);
                            } else {
                                listCode.add("");
                                dtos = transactionReceiveService
                                        .getAllTransTerminalWithType2(bankId, userId, listCode, fromDate, toDate, offset);
                            }
                            httpStatus = HttpStatus.OK;
                            break;
                        default:
                            logger.error("getTransactionUser: ERROR: INVALID TYPE");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            break;
                    }
                } else {
                    result = new ArrayList<>();
                    httpStatus = HttpStatus.OK;
                }
                String bankShortName = accountBankReceiveService.getBankShortNameByBankId(bankId);
                boolean isActiveService = accountBankReceiveService.checkIsActiveService(bankId);
                if (isActiveService) {
                    result = dtos.stream().map(dto -> {
                        TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                        responseDTO.setTransactionId(dto.getTransactionId());
                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                        responseDTO.setBankAccount(dto.getBankAccount());
                        responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                        responseDTO.setTransType(dto.getTransType());
                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                        responseDTO.setStatus(dto.getStatus());
                        responseDTO.setTime(dto.getTime());
                        responseDTO.setTimePaid(dto.getTimePaid());
                        responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                        responseDTO.setContent(dto.getContent());
                        responseDTO.setType(dto.getType());
                        responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                        return responseDTO;

                    }).collect(Collectors.toList());
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    long time = now.toEpochSecond(ZoneOffset.UTC);
                    if (!dtos.isEmpty()) {
                        time = dtos.get(0).getTime();
                    }
                    SystemSettingEntity setting = systemSettingService.getSystemSetting();
                    if (setting.getServiceActive() > time) {
                        result = dtos.stream().map(dto -> {
                            TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                            responseDTO.setTransactionId(dto.getTransactionId());
                            responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                            responseDTO.setBankAccount(dto.getBankAccount());
                            responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                            responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                            responseDTO.setTransType(dto.getTransType());
                            responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                            responseDTO.setStatus(dto.getStatus());
                            responseDTO.setTime(dto.getTime());
                            responseDTO.setTimePaid(dto.getTimePaid());
                            responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                            responseDTO.setContent(dto.getContent());
                            responseDTO.setType(dto.getType());
                            responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                            return responseDTO;

                        }).collect(Collectors.toList());
                    } else {
                        if (!dtos.isEmpty()) {
                            int lastIndex = dtos.size() - 1;
                            long lastTime = dtos.get(lastIndex).getTime();
                            TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(bankId);
                            if (entity != null) {
                                if (entity.getLastTimes() <= lastTime) {
                                    result = dtos.stream().map(dto -> {
                                        TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                        responseDTO.setTransactionId(dto.getTransactionId());
                                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                        responseDTO.setBankAccount(dto.getBankAccount());
                                        responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                        responseDTO.setTransType(dto.getTransType());
                                        responseDTO.setAmount("*****");
                                        responseDTO.setStatus(dto.getStatus());
                                        responseDTO.setTime(dto.getTime());
                                        responseDTO.setTimePaid(dto.getTimePaid());
                                        responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                        responseDTO.setContent(dto.getContent());
                                        responseDTO.setType(dto.getType());
                                        responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                        return responseDTO;

                                    }).collect(Collectors.toList());
                                } else {
                                    result = dtos.stream().map(dto -> {
                                        TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                        responseDTO.setTransactionId(dto.getTransactionId());
                                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                        responseDTO.setBankAccount(dto.getBankAccount());
                                        responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                        responseDTO.setTransType(dto.getTransType());
                                        if (entity.getTransIds().contains(dto.getTransactionId())) {
                                            responseDTO.setAmount(dto.getAmount());
                                        } else if (dto.getTime() < entity.getLastTimes()) {
                                            responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                        } else {
                                            responseDTO.setAmount("*****");
                                        }
                                        responseDTO.setStatus(dto.getStatus());
                                        responseDTO.setTime(dto.getTime());
                                        responseDTO.setTimePaid(dto.getTimePaid());
                                        responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                        responseDTO.setContent(dto.getContent());
                                        responseDTO.setType(dto.getType());
                                        responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                        return responseDTO;

                                    }).collect(Collectors.toList());
                                }
                            } else {
                                result = dtos.stream().map(dto -> {
                                    TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setBankAccount(dto.getBankAccount());
                                    responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    responseDTO.setAmount("*****");
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            }

                        }
                    }
                }

            } else {
                logger.error("getTransactionUser: ERROR: INVALID USER");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/transactions-export")
    public ResponseEntity<byte[]> exportTransactions(
            @RequestParam(value = "merchantId") String merchantId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            HttpServletResponse response) {
        try {

            List<TransactionReceiveAdminListDTO> list = new ArrayList<>();
            // type (search by)
            // - 0: bankAccount
            // - 1: reference_number (FT Code)
            // - 2: order_id
            // - 3: content
            // - 4: terminal code
            // - 9: all
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
            if (checkEmptyDate) {
                switch (type) {
                    case 0:
                        list = transactionReceiveService.exportTransByBankAccountAllDate(value);
                        break;
                    case 1:
                        list = transactionReceiveService.exportTransByFtCodeAndMerchantId(value, merchantId);
                        break;
                    case 2:
                        list = transactionReceiveService.exportTransByOrderIdAndMerchantId(value, merchantId);
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        list = transactionReceiveService.exportTransByContentAndMerchantId(value, merchantId);
                        break;
                    case 4:
                        list = transactionReceiveService.exportTransByTerminalCodeAndMerchantIdAllDate(value, merchantId);
                        break;
                    case 9:
                        list = transactionReceiveService.exportAllTransAllDateByMerchantId(merchantId);
                        break;
                    default:
                        logger.error("exportTransactions: ERROR: INVALID TYPE");
                        // Trả về lỗi nếu loại không hợp lệ
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                switch (type) {
                    case 0:
                        list = transactionReceiveService.exportTransByBankAccountFromDate(value, fromDate, toDate);
                        break;
                    case 1:
                        list = transactionReceiveService.exportTransByFtCodeAndMerchantId(value, merchantId, fromDate, toDate);
                        break;
                    case 2:
                        list = transactionReceiveService.exportTransByOrderIdAndMerchantId(value, merchantId, fromDate, toDate);
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        list = transactionReceiveService.exportTransByContentAndMerchantId(value, merchantId, fromDate, toDate);
                        break;
                    case 4:
                        list = transactionReceiveService.exportTransFromDateByTerminalCodeAndMerchantId(fromDate, toDate,
                                value, merchantId);
                        break;
                    case 9:
                        list = transactionReceiveService.exportAllTransFromDateByMerchantId(fromDate, toDate, merchantId);
                        break;
                    default:
                        logger.error("exportTransactions: ERROR: INVALID TYPE");
                        // Trả về lỗi nếu loại không hợp lệ
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (list.isEmpty()) {
                logger.error("exportTransactions: ERROR: NO DATA FOUND");
                // Trả về lỗi nếu không tìm thấy dữ liệu
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("VietQRVN-Transaction");

                // Tạo hàng tiêu đề
                Row headerRow = sheet.createRow(0);
                String[] headers = {"STT", "Số TK", "Ngân hàng", "Mã đơn hàng", "Mã mã GD", "Thu (VND)", "Chi (VND)",
                        "Trạng thái",
                        "Thời gian tạo GD", "Thời gian TT", "Mã điểm bán", "Nội dung", "Loại GD", "Ghi chú"};

                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                int counter = 1;
                for (TransactionReceiveAdminListDTO item : list) {
                    Row row = sheet.createRow(counter++);
                    row.createCell(0).setCellValue(String.valueOf(counter));
                    row.createCell(1).setCellValue(item.getBankAccount());
                    row.createCell(2).setCellValue(item.getBankShortName());
                    row.createCell(3).setCellValue(
                            item.getOrderId() != null && !item.getOrderId().trim().isEmpty() ? item.getOrderId() : "");
                    row.createCell(4)
                            .setCellValue(
                                    item.getReferenceNumber() != null && !item.getReferenceNumber().trim().isEmpty()
                                            ? item.getReferenceNumber()
                                            : "");
                    if (item.getTransType().equalsIgnoreCase("C")) {
                        row.createCell(5).setCellValue(item.getAmount());
                        row.createCell(6).setCellValue("");
                    } else {
                        row.createCell(5).setCellValue("");
                        row.createCell(6).setCellValue(item.getAmount());
                    }
                    String status = getStatusText(item.getStatus());
                    row.createCell(7).setCellValue(status);
                    row.createCell(8).setCellValue(generateTime(item.getTimeCreated(), false));
                    boolean isNotFormat = false;
                    if (item.getStatus() != 1) {
                        isNotFormat = true;
                    }
                    row.createCell(9).setCellValue(generateTime(item.getTimePaid(), isNotFormat));
                    row.createCell(10)
                            .setCellValue(item.getTerminalCode() != null && !item.getTerminalCode().trim().isEmpty()
                                    ? item.getTerminalCode()
                                    : "-");
                    row.createCell(11).setCellValue(item.getContent());
                    String typeTrans = getTransactionTypeText(item.getType());
                    row.createCell(12).setCellValue(typeTrans);
                    row.createCell(13).setCellValue(item.getNote() != null ? item.getNote() : "");
                }

                // Tạo một mảng byte từ workbook
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                byte[] fileContent = outputStream.toByteArray();

                // Thiết lập các thông số của response
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");
                response.setContentLength(fileContent.length);

                // Ghi dữ liệu vào response
                response.getOutputStream().write(fileContent);
            }
            response.getOutputStream().flush();

            // Trả về response thành công
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            logger.error("exportTransactions: ERROR: " + e.getMessage());
            // Trả về lỗi nếu có lỗi khi ghi tệp Excel
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("exportTransactions: ERROR: " + e.toString());
            System.out.println("exportTransactions: ERROR: " + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0:
                return "Chờ thanh toán";
            case 1:
                return "Thành công";
            case 2:
                return "Đã huỷ";
            default:
                return "";
        }
    }

    private String getTransactionTypeText(int type) {
        switch (type) {
            case 0:
                return "Mã VietQR";
            case 2:
                return "Khác";
            default:
                return "-";
        }
    }

    String formatCurrency(double amount) {
        String result = "";
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            result = formatter.format(amount);
        } catch (Exception e) {
            logger.error("ERROR AT generateTime: " + e.toString());
        }
        return result;
    }

    String generateTime(Long input, Boolean isNotFormat) {
        String result = "";
        try {
            if (isNotFormat == true) {
                result = "-";
            } else {
                if (input != null) {
                    // Chuyển đổi thời gian từ kiểu long thành đối tượng Instant
                    Instant instant = Instant.ofEpochSecond(input);

                    // Chuyển đổi thời gian từ Instant sang múi giờ UTC+7
                    ZoneId zoneId = ZoneId.of("UTC+7");
                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);

                    // Định dạng chuỗi ngày tháng
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy\nHH:mm:ss");

                    // Lấy thời gian trong múi giờ UTC+7 dưới dạng chuỗi
                    String formattedDateTime = zonedDateTime.format(formatter);

                    result = formattedDateTime;
                    if (result.trim().contains("01/01/1970")) {
                        result = "-";
                    }
                }
            }

        } catch (Exception e) {
            logger.error("ERROR AT generateTime: " + e.toString());
        }
        return result;
    }

    // get trans receive admin detail
    @GetMapping("admin/transaction")
    public ResponseEntity<TransReceiveAdminDetailDTO> getTransactionDetailAdmin(
            @RequestParam(value = "id") String id) {
        TransReceiveAdminDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.getDetailTransReceiveAdmin(id);
            if (result != null) {
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getTransactionDetailAdmin: NOT FOUND DETAIL TRANSACTION");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getTransactionDetailAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get log callback by transaction id
    @GetMapping("admin/transaction/log")
    public ResponseEntity<List<TransactionReceiveLogEntity>> getTransactionReceiveLogsByTransId(
            @RequestParam(value = "id") String id) {
        List<TransactionReceiveLogEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveLogService.getTransactionReceiveLogsByTransId(id);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionReceiveLogsByTransId: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction/list")
    public ResponseEntity<List<TransactionRelatedDTO>> getTransactionsByBankId(
            @Valid @RequestBody TransactionInputDTO dto) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (StringUtil.isEmptyOrEqualsZero(dto.getFrom()) || StringUtil.isEmptyOrEqualsZero(dto.getTo())) {
                result = transactionReceiveService.getTransactions(dto.getOffset(), dto.getBankId());
            } else {
                result = transactionReceiveService.getTransactions(dto.getOffset(), dto.getBankId(),
                        dto.getFrom(), dto.getTo());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transactions/unsettled")
    public ResponseEntity<List<TransactionRelatedRequestDTO>> getUnsettledTransactions(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        List<TransactionRelatedRequestDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 4: terminal code
            // type = 5: status
            List<TransactionReceiveAdminListDTO> dtos = new ArrayList<>();
            List<String> roleList = new ArrayList<>();
            roleList.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
            roleList.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
            roleList.add(EnvironmentUtil.getAdminRoleId());
            String roles = String.join("|", roleList);
            String isOwner = merchantMemberRoleService.checkMemberHaveRole(userId, roles);
            if (isOwner == null || isOwner.isEmpty()) {
                isOwner = accountBankReceiveShareService.checkUserExistedFromBankAccountAndIsOwner(userId, bankId);
            }
            if (isOwner != null && !isOwner.isEmpty()) {
                switch (type) {
                    case 9:
                        dtos = transactionReceiveService.getUnsettledTransactions(bankId, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        dtos = transactionReceiveService.getUnsettledTransactionsByFtCode(bankId, value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        dtos = transactionReceiveService.getUnsettledTransactionsByOrderId(bankId, value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        dtos = transactionReceiveService.getUnsettledTransactionsByContent(bankId, value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 4:
                        dtos = transactionReceiveService.getUnsettledTransactionsByTerminalCode(bankId, value, fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    case 5:
                        dtos = transactionReceiveService.getUnsettledTransactionsByStatus(bankId, Integer.parseInt(value), fromDate, toDate, offset);
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getUnsettledTransactions: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
                if (dtos != null && !dtos.isEmpty()) {
                    List<String> listTransId = dtos.stream().map(TransactionReceiveAdminListDTO::getId)
                            .collect(Collectors.toList());
                    List<TransRequestDTO> listRequests = transReceiveRequestMappingService
                            .getTransactionReceiveRequest(listTransId);
                    Map<String, List<TransRequestDTO>> terminalBanksMap = listRequests.stream()
                            .collect(Collectors.groupingBy(TransRequestDTO::getTransactionId));

                    boolean isActiveService = accountBankReceiveService.checkIsActiveService(bankId);
                    if (isActiveService) {
                        result = dtos.stream().map(item -> {
                            TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                            trans.setId(item.getId());
                            trans.setBankAccount(item.getBankAccount());
                            trans.setAmount(formatAmountNumber(item.getAmount() + ""));
                            trans.setBankId(item.getBankId());
                            trans.setContent(item.getContent());
                            trans.setOrderId(item.getOrderId());
                            trans.setReferenceNumber(item.getReferenceNumber());
                            trans.setStatus(item.getStatus());
                            trans.setTimeCreated(item.getTimeCreated());
                            trans.setTimePaid(item.getTimePaid());
                            trans.setTransType(item.getTransType());
                            trans.setType(item.getType());
                            trans.setUserBankName(item.getUserBankName());
                            trans.setBankShortName(item.getBankShortName());
                            trans.setTerminalCode(item.getTerminalCode());
                            trans.setNote(item.getNote());
                            trans.setRequests(terminalBanksMap
                                    .getOrDefault(item.getId(), new ArrayList<>()));
                            trans.setTotalRequest(trans.getRequests().size());
                            return trans;
                        }).collect(Collectors.toList());
                    } else {
                        LocalDateTime now = LocalDateTime.now();
                        long time = now.toEpochSecond(ZoneOffset.UTC);
                        if (!dtos.isEmpty()) {
                            time = dtos.get(0).getTimeCreated();
                        }
                        SystemSettingEntity setting = systemSettingService.getSystemSetting();
                        if (setting.getServiceActive() > time) {
                            result = dtos.stream().map(item -> {
                                TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                                trans.setId(item.getId());
                                trans.setBankAccount(item.getBankAccount());
                                trans.setAmount(formatAmountNumber(item.getAmount() + ""));
                                trans.setBankId(item.getBankId());
                                trans.setContent(item.getContent());
                                trans.setOrderId(item.getOrderId());
                                trans.setReferenceNumber(item.getReferenceNumber());
                                trans.setStatus(item.getStatus());
                                trans.setTimeCreated(item.getTimeCreated());
                                trans.setTimePaid(item.getTimePaid());
                                trans.setTransType(item.getTransType());
                                trans.setType(item.getType());
                                trans.setUserBankName(item.getUserBankName());
                                trans.setBankShortName(item.getBankShortName());
                                trans.setTerminalCode(item.getTerminalCode());
                                trans.setNote(item.getNote());
                                trans.setRequests(terminalBanksMap
                                        .getOrDefault(item.getId(), new ArrayList<>()));
                                trans.setTotalRequest(trans.getRequests().size());
                                return trans;
                            }).collect(Collectors.toList());
                        } else {
                            if (!dtos.isEmpty()) {
                                int lastIndex = dtos.size() - 1;
                                long lastTime = dtos.get(lastIndex).getTimeCreated();
                                TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(bankId);
                                if (entity != null) {
                                    if (entity.getLastTimes() <= lastTime) {
                                        result = dtos.stream().map(item -> {
                                            TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                                            trans.setId(item.getId());
                                            trans.setBankAccount(item.getBankAccount());
                                            trans.setAmount("*****");
                                            trans.setBankId(item.getBankId());
                                            trans.setContent(item.getContent());
                                            trans.setOrderId(item.getOrderId());
                                            trans.setReferenceNumber(item.getReferenceNumber());
                                            trans.setStatus(item.getStatus());
                                            trans.setTimeCreated(item.getTimeCreated());
                                            trans.setTimePaid(item.getTimePaid());
                                            trans.setTransType(item.getTransType());
                                            trans.setType(item.getType());
                                            trans.setUserBankName(item.getUserBankName());
                                            trans.setBankShortName(item.getBankShortName());
                                            trans.setTerminalCode(item.getTerminalCode());
                                            trans.setNote(item.getNote());
                                            trans.setRequests(terminalBanksMap
                                                    .getOrDefault(item.getId(), new ArrayList<>()));
                                            trans.setTotalRequest(trans.getRequests().size());
                                            return trans;
                                        }).collect(Collectors.toList());
                                    } else {
                                        result = dtos.stream().map(item -> {
                                            TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                                            trans.setId(item.getId());
                                            trans.setBankAccount(item.getBankAccount());
                                            if (entity.getTransIds().contains(item.getId())) {
                                                trans.setAmount(formatAmountNumber(item.getAmount() + ""));
                                            } else if (item.getTimeCreated() < entity.getLastTimes()) {
                                                trans.setAmount(formatAmountNumber(item.getAmount() + ""));
                                            } else {
                                                trans.setAmount("*****");
                                            }
                                            trans.setBankId(item.getBankId());
                                            trans.setContent(item.getContent());
                                            trans.setOrderId(item.getOrderId());
                                            trans.setReferenceNumber(item.getReferenceNumber());
                                            trans.setStatus(item.getStatus());
                                            trans.setTimeCreated(item.getTimeCreated());
                                            trans.setTimePaid(item.getTimePaid());
                                            trans.setTransType(item.getTransType());
                                            trans.setType(item.getType());
                                            trans.setUserBankName(item.getUserBankName());
                                            trans.setBankShortName(item.getBankShortName());
                                            trans.setTerminalCode(item.getTerminalCode());
                                            trans.setNote(item.getNote());
                                            trans.setRequests(terminalBanksMap
                                                    .getOrDefault(item.getId(), new ArrayList<>()));
                                            trans.setTotalRequest(trans.getRequests().size());
                                            return trans;
                                        }).collect(Collectors.toList());
                                    }
                                } else {
                                    result = dtos.stream().map(item -> {
                                        TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                                        trans.setId(item.getId());
                                        trans.setBankAccount(item.getBankAccount());
                                        trans.setAmount("*****");
                                        trans.setBankId(item.getBankId());
                                        trans.setContent(item.getContent());
                                        trans.setOrderId(item.getOrderId());
                                        trans.setReferenceNumber(item.getReferenceNumber());
                                        trans.setStatus(item.getStatus());
                                        trans.setTimeCreated(item.getTimeCreated());
                                        trans.setTimePaid(item.getTimePaid());
                                        trans.setTransType(item.getTransType());
                                        trans.setType(item.getType());
                                        trans.setUserBankName(item.getUserBankName());
                                        trans.setBankShortName(item.getBankShortName());
                                        trans.setTerminalCode(item.getTerminalCode());
                                        trans.setNote(item.getNote());
                                        trans.setRequests(terminalBanksMap
                                                .getOrDefault(item.getId(), new ArrayList<>()));
                                        trans.setTotalRequest(trans.getRequests().size());
                                        return trans;
                                    }).collect(Collectors.toList());
                                }

                            }
                        }
                    }
                }
            } else {
                result = new ArrayList<>();
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("getUnsettledTransactions: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transactions/sub-terminal/{terminalCode}")
    public ResponseEntity<Object> getTransactionBySubTerminalCode(
            @PathVariable("terminalCode") String terminalCode,
            @RequestParam(value = "subTerminalCode") String subTerminalCode,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        PageResultDTO result = new PageResultDTO();
        List<TransactionRelatedDTO> dtos = new ArrayList<>();
        List<TransactionRelatedResDTO> responses = new ArrayList<>();
        double totalPage = 0;
        int totalElement = 0;
        HttpStatus httpStatus = null;

        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 5: status
            // type = 6: amount
            List<String> codes = new ArrayList<>();
            if (subTerminalCode != null && !subTerminalCode.isEmpty()) {
                codes.add(subTerminalCode);
            } else {
                codes = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                codes.add(terminalCode);
            }
            switch (type) {
                case 9:
                    dtos = transactionReceiveService.getSubTerminalTransactions(codes,
                            fromDate, toDate, (page - 1) * size, size);
                    totalElement = transactionReceiveService.countSubTerminalTransactions(codes, fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                case 1:
                    dtos = transactionReceiveService
                            .getSubTerminalTransactionsByFtCode(codes, value,
                                    fromDate, toDate, (page - 1) * size, size);
                    totalElement = transactionReceiveService
                            .countSubTerminalTransactionsByFtCode(codes, value, fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                case 2:
                    dtos = transactionReceiveService
                            .getSubTerminalTransactionsByOrderId(codes, value, fromDate, toDate,
                                    (page - 1) * size, size);
                    totalElement = transactionReceiveService
                            .countSubTerminalTransactionsByOrderId(codes, value, fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                case 3:
                    value = value.replace("-", " ").trim();
                    dtos = transactionReceiveService
                            .getSubTerminalTransactionsByContent(codes, value, fromDate, toDate,
                                    (page - 1) * size, size);
                    totalElement = transactionReceiveService
                            .countSubTerminalTransactionsByContent(codes, value, fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                case 5:
                    dtos = transactionReceiveService
                            .getSubTerminalTransactionsByStatus(codes, Integer.parseInt(value), fromDate, toDate,
                                    (page - 1) * size, size);
                    totalElement = transactionReceiveService
                            .countSubTerminalTransactionsByStatus(codes, Integer.parseInt(value), fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                case 6:
                    dtos = transactionReceiveService
                            .getSubTerminalTransactionsByAmount(codes, Integer.parseInt(value), fromDate, toDate,
                                    (page - 1) * size, size);
                    totalElement = transactionReceiveService
                            .countSubTerminalTransactionsByAmount(codes, Integer.parseInt(value), fromDate, toDate);
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    logger.error("getUnsettledTransactions: ERROR: INVALID TYPE");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
            totalPage = (double) totalElement / size;
            String bankId = terminalBankReceiveService.getBankIdByTerminalCode(terminalCode);
            boolean isActiveService = accountBankReceiveService.checkIsActiveService(bankId);
            if (isActiveService) {
                responses = dtos.stream().map(dto -> {
                    TransactionRelatedResDTO responseDTO = new TransactionRelatedResDTO();
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                    responseDTO.setBankAccount(dto.getBankAccount());
                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                    responseDTO.setTransType(dto.getTransType());
                    responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                    responseDTO.setStatus(dto.getStatus());
                    responseDTO.setTime(dto.getTime());
                    responseDTO.setBankShortName(dto.getBankShortName());
                    responseDTO.setTimePaid(dto.getTimePaid());
                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                    responseDTO.setContent(dto.getContent());
                    responseDTO.setType(dto.getType());
                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                    return responseDTO;

                }).collect(Collectors.toList());
            } else {
                LocalDateTime now = LocalDateTime.now();
                long time = now.toEpochSecond(ZoneOffset.UTC);
                if (!dtos.isEmpty()) {
                    time = dtos.get(0).getTime();
                }
                SystemSettingEntity setting = systemSettingService.getSystemSetting();
                if (setting.getServiceActive() > time) {
                    responses = dtos.stream().map(dto -> {
                        TransactionRelatedResDTO responseDTO = new TransactionRelatedResDTO();
                        responseDTO.setTransactionId(dto.getTransactionId());
                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                        responseDTO.setBankAccount(dto.getBankAccount());
                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                        responseDTO.setTransType(dto.getTransType());
                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                        responseDTO.setStatus(dto.getStatus());
                        responseDTO.setTime(dto.getTime());
                        responseDTO.setTimePaid(dto.getTimePaid());
                        responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                        responseDTO.setContent(dto.getContent());
                        responseDTO.setBankShortName(dto.getBankShortName());
                        responseDTO.setType(dto.getType());
                        responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                        return responseDTO;

                    }).collect(Collectors.toList());
                } else {
                    if (!dtos.isEmpty()) {
                        int lastIndex = dtos.size() - 1;
                        long lastTime = dtos.get(lastIndex).getTime();
                        TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(bankId);
                        if (entity != null) {
                            if (entity.getLastTimes() <= lastTime) {
                                responses = dtos.stream().map(dto -> {
                                    TransactionRelatedResDTO responseDTO = new TransactionRelatedResDTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setBankAccount(dto.getBankAccount());
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    responseDTO.setAmount("*****");
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setBankShortName(dto.getBankShortName());
                                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            } else {
                                responses = dtos.stream().map(dto -> {
                                    TransactionRelatedResDTO responseDTO = new TransactionRelatedResDTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setBankAccount(dto.getBankAccount());
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    if (entity.getTransIds().contains(dto.getTransactionId())) {
                                        responseDTO.setAmount(dto.getAmount());
                                    } else if (dto.getTime() < entity.getLastTimes()) {
                                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                    } else {
                                        responseDTO.setAmount("*****");
                                    }
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setBankShortName(dto.getBankShortName());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            }
                        } else {
                            responses = dtos.stream().map(dto -> {
                                TransactionRelatedResDTO responseDTO = new TransactionRelatedResDTO();
                                responseDTO.setTransactionId(dto.getTransactionId());
                                responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                responseDTO.setBankAccount(dto.getBankAccount());
                                responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                responseDTO.setTransType(dto.getTransType());
                                responseDTO.setAmount("*****");
                                responseDTO.setStatus(dto.getStatus());
                                responseDTO.setTime(dto.getTime());
                                responseDTO.setTimePaid(dto.getTimePaid());
                                responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                responseDTO.setContent(dto.getContent());
                                responseDTO.setBankShortName(dto.getBankShortName());
                                responseDTO.setType(dto.getType());
                                responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                return responseDTO;

                            }).collect(Collectors.toList());
                        }

                    }
                }
            }

            result = new PageResultDTO(page, size, (int) Math.ceil(totalPage), totalElement, responses);

        } catch (Exception e) {
            logger.error("getTransactionBySubTerminalCode: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction/overview-sub-terminal/{terminalCode}")
    public ResponseEntity<TransStatisticResponseDTO> getTransactionSubTerminalCodeOverview(
            @PathVariable("terminalCode") String subTerminalCode,
            @RequestParam(value = "subTerminalCode") String terminalCode,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        TransStatisticResponseDTO result = null;
        TransStatisticDTO dto = null;
        HttpStatus httpStatus = null;
        try {
            if (StringUtil.isNullOrEmpty(subTerminalCode)) {
                List<String> codes = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                codes.add(terminalCode);
                dto = transactionReceiveService
                        .getTransactionOverviewBySubTerminalCode(codes, fromDate, toDate);
            } else {
                dto = transactionReceiveService
                        .getTransactionOverviewBySubTerminalCode(terminalCode, fromDate, toDate);
            }
            if (dto != null && Objects.nonNull(dto.getTotalCashIn()) && Objects.nonNull(dto.getTotalCashOut())) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;
            } else {
                result = new TransStatisticResponseDTO();
                result.setTotalTrans(0L);
                result.setTotalTransC(0L);
                result.setTotalTransD(0L);
                result.setTotalCashIn(0L);
                result.setTotalCashOut(0L);
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            System.out.println("Error at getTransactionSubTerminalCodeOverview: " + e.toString());
            logger.error("Error at getTransactionSubTerminalCodeOverview: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction/statistic-sub-terminal/{subTerminalCode}")
    public ResponseEntity<List<TransStatisticByTimeDTO>> getTransactionSubTerminalCodeStatistic(
            @PathVariable(value = "subTerminalCode") String subTerminalCode,
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        List<TransStatisticByTimeDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            switch (type) {
                // by date
                case 0:
                    if (StringUtil.isNullOrEmpty(subTerminalCode)) {
                        List<String> codes = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                        codes.add(terminalCode);
                        result = transactionReceiveService
                                .getTransStatisticSubTerminalByTerminalCodeDate(codes, fromDate, toDate);
                    } else {
                        result = transactionReceiveService
                                .getTransStatisticSubTerminalByTerminalCodeDate(subTerminalCode, fromDate, toDate);
                    }
                    httpStatus = HttpStatus.OK;
                    break;
                // by month
                case 1:
                    if (StringUtil.isNullOrEmpty(subTerminalCode)) {
                        List<String> codes = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                        codes.add(terminalCode);
                        result = transactionReceiveService
                                .getTransStatisticSubTerminalByTerminalCodeMonth(codes, fromDate, toDate);
                    } else {
                        result = transactionReceiveService
                                .getTransStatisticSubTerminalByTerminalCodeMonth(subTerminalCode, fromDate, toDate);
                    }
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    logger.error("getTransactionSubTerminalCodeStatistic: ERROR: INVALID TYPE");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error at getTransactionSubTerminalCodeOverview: " + e.toString());
            logger.error("Error at getTransactionSubTerminalCodeOverview: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction/map-terminal")
    public ResponseEntity<ResponseMessageDTO> mapTransactionToTerminal(
            @Valid @RequestBody MapTransactionToTerminalDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            TransactionUpdateTerminalDTO data1 = new TransactionUpdateTerminalDTO();
            TransactionUpdateTerminalDTO data2 = new TransactionUpdateTerminalDTO();
            TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                    .getTransactionReceiveById(dto.getTransactionId());
            if (transactionReceiveEntity == null) {
                result = new ResponseMessageDTO("FAILED", "E115");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                if (transactionReceiveEntity.getType() == 0) {
                    //update terminal code
                    transactionReceiveService.updateTransactionReceiveTerminal(dto.getTransactionId(), dto.getTerminalCode(), 0);
                    data1.setTransactionId(dto.getTransactionId());
                    data1.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                    data1.setType(transactionReceiveEntity.getType());
//                    data1.setTransStatus(transactionReceiveEntity.getTransStatus());
                    data2.setTransactionId(dto.getTransactionId());
                    data2.setTerminalCode(dto.getTerminalCode());
                    data2.setType(transactionReceiveEntity.getType());
//                    data2.setTransStatus(transactionReceiveEntity.getTransStatus());
                } else {
                    // update terminal code
                    transactionReceiveService.updateTransactionReceiveTerminal(dto.getTransactionId(), dto.getTerminalCode(), 1);
                    data1.setTransactionId(dto.getTransactionId());
                    data1.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                    data1.setType(transactionReceiveEntity.getType());
//                    data1.setTransStatus(transactionReceiveEntity.getTransStatus());
                    data2.setTransactionId(dto.getTransactionId());
                    data2.setTerminalCode(dto.getTerminalCode());
                    data2.setType(1);
//                    data2.setTransStatus(transactionReceiveEntity.getTransStatus());
                }

                // insert for statistic
                if ("C".equalsIgnoreCase(transactionReceiveEntity.getTransType())
                        && 1 == transactionReceiveEntity.getStatus()) {
                    TransactionTerminalTempEntity transactionTerminalTemp = transactionTerminalTempService
                            .getTempByTransactionId(dto.getTransactionId());
                    if (transactionTerminalTemp != null) {
                        transactionTerminalTemp.setTerminalCode(dto.getTerminalCode());
                    } else {
                        transactionTerminalTemp = new TransactionTerminalTempEntity();
                        transactionTerminalTemp.setId(UUID.randomUUID().toString());
                        transactionTerminalTemp.setTransactionId(dto.getTransactionId());
                        transactionTerminalTemp.setTerminalCode(dto.getTerminalCode());
                        transactionTerminalTemp.setTime(transactionReceiveEntity.getTimePaid());
                        transactionTerminalTemp.setAmount(transactionReceiveEntity.getAmount());
                    }
                    transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTemp);
                }

                // save history
                TransactionReceiveHistoryEntity transHistory = new TransactionReceiveHistoryEntity();
                transHistory.setId(UUID.randomUUID().toString());
                transHistory.setTransactionReceiveId(dto.getTransactionId());
                transHistory.setUserId(dto.getUserId());
                transHistory.setData1(mapper.writeValueAsString(data1));
                transHistory.setData2(mapper.writeValueAsString(data2));
                transHistory.setData3("");
                transHistory.setType(2);
                LocalDateTime localDateTime = LocalDateTime.now();
                long timeUpdated = localDateTime.toEpochSecond(ZoneOffset.UTC);
                transHistory.setTimeUpdated(timeUpdated);
                transactionReceiveHistoryService.insertTransactionReceiveHistory(transHistory);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E46");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @GetMapping("transactions")
    public ResponseEntity<List<TransactionRelatedDTO>> getTransactionsFilter(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (status == 9) {
                result = transactionReceiveService.getTransactions(offset, bankId);
            } else {
                if (StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate)) {
                    result = transactionReceiveService.getTransactionsByStatus(status, offset, bankId);
                } else {
                    result = transactionReceiveService.getTransactionsByStatus(status, offset, bankId, fromDate, toDate);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // chua phan quyen
    @GetMapping("transactions/web/overview")
    public ResponseEntity<TransStatisticResponseWebDTO> getTransactionOverviewWeb(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        TransStatisticResponseWebDTO result = null;
        HttpStatus httpStatus = null;
        try {
//            String isOwner = accountBankReceiveService.checkIsOwner(bankId, userId);
            ITransStatisticResponseWebDTO dto = null;
            dto = transactionReceiveService
                    .getTransactionWebOverview(bankId, fromDate, toDate);
            if (dto != null) {
                result = new TransStatisticResponseWebDTO();
                result.setTotalTrans(dto.getTotalTrans());
                result.setTotalCashIn(dto.getTotalCashIn());
                result.setTotalCashSettled(dto.getTotalCashSettled());
                result.setTotalSettled(dto.getTotalSettled());
                result.setTotalUnsettled(dto.getTotalUnsettled());
                result.setTotalCashUnsettled(dto.getTotalCashUnsettled());
            } else {
                result = new TransStatisticResponseWebDTO();
                result.setTotalTrans(0);
                result.setTotalCashIn(0L);
                result.setTotalCashSettled(0L);
                result.setTotalSettled(0);
                result.setTotalUnsettled(0);
                result.setTotalCashUnsettled(0L);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionOverviewWeb: ERROR: " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("transactions/list")
    public ResponseEntity<List<TransactionRelatedResponseDTO>> getTransactionsMobile(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to,
            @RequestParam(value = "offset") int offset) {
        List<TransactionRelatedResponseDTO> result = new ArrayList<>();
        List<TransactionRelatedDTO> dtos = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 4: terminal code
            // type = 5: status
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(from) || StringUtil.isEmptyOrEqualsZero(to);
            if (checkEmptyDate) {
                switch (type) {
                    case 9:
                        dtos = transactionReceiveService.getTransactions(offset, bankId);
                        break;
                    case 1:
                        dtos = transactionReceiveService.getTransactionsByFtCode(value, offset, bankId);
                        break;
                    case 2:
                        dtos = transactionReceiveService.getTransactionsByOrderId(value, offset, bankId);
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        dtos = transactionReceiveService.getTransactionsByContent(value, offset, bankId);
                        break;
                    case 4:
                        String terminalCodeForSearch = "";
                        List<String> allTerminalCode = new ArrayList<>();
                        terminalCodeForSearch = terminalService.getTerminalCodeByTerminalCode(value);
                        if (terminalCodeForSearch == null || terminalCodeForSearch.trim().isEmpty()) {
                            terminalCodeForSearch = terminalBankReceiveService.getTerminalCodeByRawTerminalCode(value);
                            if (terminalCodeForSearch == null || terminalCodeForSearch.isEmpty()) {
                                terminalCodeForSearch = value;
                            }
                        } else {
                            allTerminalCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCodeForSearch);
                            allTerminalCode.add(terminalCodeForSearch);
                        }
                        if (!allTerminalCode.isEmpty()) {
                            dtos = transactionReceiveService.getTransactionsByTerminalCodeAllDateListCode(allTerminalCode, offset, bankId);
                        } else {
                            dtos = transactionReceiveService.getTransactionsByTerminalCodeAllDate(terminalCodeForSearch, offset, bankId);
                        }
                        break;
                    case 5:
                        Integer status = Integer.parseInt(value);
                        dtos = transactionReceiveService.getTransactionsByStatus(status, offset, bankId);
                        break;
                    default:
                        logger.error("getTransactionsMobile: ERROR: INVALID TYPE");
                        break;
                }
            } else {
                switch (type) {
                    case 9:
                        dtos = transactionReceiveService.getTransactions(offset, bankId, from, to);
                        break;
                    case 1:
                        dtos = transactionReceiveService.getTransactionsByFtCode(value, offset, bankId, from, to);
                        break;
                    case 2:
                        dtos = transactionReceiveService.getTransactionsByOrderId(value, offset, bankId, from, to);
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        dtos = transactionReceiveService.getTransactionsByContent(value, offset, bankId, from, to);
                        break;
                    case 4:
                        String terminalCodeForSearch = "";
                        List<String> allTerminalCode = new ArrayList<>();
                        terminalCodeForSearch = terminalService.getTerminalCodeByTerminalCode(value);
                        if (terminalCodeForSearch == null || terminalCodeForSearch.trim().isEmpty()) {
                            terminalCodeForSearch = terminalBankReceiveService.getTerminalCodeByRawTerminalCode(value);
                            if (terminalCodeForSearch == null || terminalCodeForSearch.isEmpty()) {
                                terminalCodeForSearch = value;
                            }
                        } else {
                            allTerminalCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCodeForSearch);
                            allTerminalCode.add(terminalCodeForSearch);
                        }
                        if (!allTerminalCode.isEmpty()) {
                            dtos = transactionReceiveService
                                    .getTransactionsByTerminalCodeAndDateListCode(allTerminalCode, offset, bankId, from, to);
                        } else {
                            dtos = transactionReceiveService
                                    .getTransactionsByTerminalCodeAndDate(terminalCodeForSearch, offset, from, to,
                                            bankId);
                        }
                        break;
                    case 5:
                        if (!StringUtil.isNullOrEmpty(value)) {
                            int status = Integer.parseInt(value);
                            dtos = transactionReceiveService.getTransactionsByStatus(status, offset, bankId, from, to);
                        } else {
                            dtos = transactionReceiveService.getTransactions(offset, bankId, from, to);
                        }
                        break;
                    default:
                        logger.error("getTransactionsMobile: ERROR: INVALID TYPE");
                        break;
                }
            }
            String bankShortName = accountBankReceiveService.getBankShortNameByBankId(bankId);
            boolean isActiveService = accountBankReceiveService.checkIsActiveService(bankId);
            if (isActiveService) {
                result = dtos.stream().map(dto -> {
                    TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                    responseDTO.setBankAccount(dto.getBankAccount());
                    responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                    responseDTO.setTransType(dto.getTransType());
                    responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                    responseDTO.setStatus(dto.getStatus());
                    responseDTO.setTime(dto.getTime());
                    responseDTO.setTimePaid(dto.getTimePaid());
                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                    responseDTO.setContent(dto.getContent());
                    responseDTO.setType(dto.getType());
                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                    return responseDTO;

                }).collect(Collectors.toList());
            } else {
                LocalDateTime now = LocalDateTime.now();
                long time = now.toEpochSecond(ZoneOffset.UTC);
                if (!dtos.isEmpty()) {
                    time = dtos.get(0).getTime();
                }
                SystemSettingEntity setting = systemSettingService.getSystemSetting();
                if (setting.getServiceActive() > time) {
                    result = dtos.stream().map(dto -> {
                        TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                        responseDTO.setTransactionId(dto.getTransactionId());
                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                        responseDTO.setBankAccount(dto.getBankAccount());
                        responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                        responseDTO.setTransType(dto.getTransType());
                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                        responseDTO.setStatus(dto.getStatus());
                        responseDTO.setTime(dto.getTime());
                        responseDTO.setTimePaid(dto.getTimePaid());
                        responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                        responseDTO.setContent(dto.getContent());
                        responseDTO.setType(dto.getType());
                        responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                        return responseDTO;

                    }).collect(Collectors.toList());
                } else {
                    if (!dtos.isEmpty()) {
                        int lastIndex = dtos.size() - 1;
                        long lastTime = dtos.get(lastIndex).getTime();
                        TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(bankId);
                        if (entity != null) {
                            if (entity.getLastTimes() <= lastTime) {
                                result = dtos.stream().map(dto -> {
                                    TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setBankAccount(dto.getBankAccount());
                                    responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    responseDTO.setAmount("*****");
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            } else {
                                result = dtos.stream().map(dto -> {
                                    TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setBankAccount(dto.getBankAccount());
                                    responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    if (entity.getTransIds().contains(dto.getTransactionId())) {
                                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                    } else if (dto.getTime() < entity.getLastTimes()) {
                                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                    } else {
                                        responseDTO.setAmount("*****");
                                    }
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            }
                        } else {
                            result = dtos.stream().map(dto -> {
                                TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                                responseDTO.setTransactionId(dto.getTransactionId());
                                responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                responseDTO.setBankAccount(dto.getBankAccount());
                                responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                                responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                responseDTO.setTransType(dto.getTransType());
                                responseDTO.setAmount("*****");
                                responseDTO.setStatus(dto.getStatus());
                                responseDTO.setTime(dto.getTime());
                                responseDTO.setTimePaid(dto.getTimePaid());
                                responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                                responseDTO.setContent(dto.getContent());
                                responseDTO.setType(dto.getType());
                                responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                                return responseDTO;

                            }).collect(Collectors.toList());
                        }

                    }
                }
            }

            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseObjectDTO getTerminalCodeAccess(String bankId, String userId) {
        ResponseObjectDTO result = null;
        try {
            if (!StringUtil.isNullOrEmpty(accountBankReceiveService.checkIsOwner(bankId, userId))) {
                // access all
                result = new ResponseObjectDTO("SUCCESS", "");
            } else {
                List<String> listCode = new ArrayList<>();
                List<String> terminalCodeAccess = new ArrayList<>();

                List<String> roles = merchantMemberRoleService.getRoleByUserIdAndBankId(userId, bankId);
                // check role có được xem giao dịch chưa xaác nhân không và là cấp nào nào
                // check level:
                // 0 : terminal, 1: merchant
                // check accept see type = 2
                int acceptSee = 0;
                int level = 0;

                if (roles != null && !roles.isEmpty()) {
                    if (roles.contains(EnvironmentUtil.getRequestReceiveMerchantRoleId())) {
                        level = 1;
                        acceptSee = 1;
                    } else if (roles.contains(EnvironmentUtil.getRequestReceiveTerminalRoleId())) {
                        acceptSee = 1;
                    } else if (roles.contains(EnvironmentUtil.getOnlyReadReceiveMerchantRoleId())) {
                        level = 1;
                        acceptSee = 1;
                    }
                }
                if (level == 0) {
                    terminalCodeAccess = terminalBankReceiveService.getTerminalCodeByUserIdAndBankId(userId, bankId);
                } else {
                    terminalCodeAccess = terminalBankReceiveService.getTerminalCodeByUserIdAndBankIdNoTerminal(userId, bankId);
                }
                listCode = terminalBankReceiveService.getTerminalCodeByMainTerminalCodeList(terminalCodeAccess);
                listCode.addAll(terminalCodeAccess);
                if (acceptSee == 1) {
                    listCode.add("");
                    listCode.add(null);
                }
                if (!listCode.isEmpty()) {
                    result =  new ResponseObjectDTO("CHECK", listCode);
                } else {
                    result = new ResponseObjectDTO("FAILED", "");
                }
            }
        } catch (Exception e) {
            result = new ResponseObjectDTO("FAILED", "");
        }
        return result;
    }

    private TypeValueFilterDTO processFilterSearch(String value, String bankId) {
        value = StringUtil.removeDiacritics(value);
        TypeValueFilterDTO result = new TypeValueFilterDTO();
        if (StringUtil.isNullOrEmpty(value)) {
            result.setType(9);
            result.setValue("");
        } else if (StringUtil.isValidRegular(value, "(?i)^(Thanh cong|Da huy|Cho TT|Cho thanh toan).*")) {
            result.setType(5);
            switch (value.toLowerCase()) {
                case "thanh cong":
                    result.setValue("1");
                    break;
                case "da huy":
                    result.setValue("2");
                    break;
                case "cho tt":
                case "cho thanh toan":
                    result.setValue("0");
                    break;
            }
        } else if (StringUtil.isValidRegular(value, "^[1-9][0-9]*00$")) {
            result.setType(6);
            result.setValue(value);
        } else if (StringUtil.isValidRegular(value, "(?i)^ft\\d{6,19}$")) {
            result.setType(1);
            result.setValue(value);
        } else if (StringUtil.isValidRegular(value, "(?i)^(CH |Cua hang ).*")) {
            result.setType(4);
            String name = value.replaceFirst("(?i)^(CH |Cua hang )", "").trim();
            List<String> codeList = terminalService.getAllCodeByNameAndBankId(name, bankId);
            if (codeList != null && !codeList.isEmpty()) {
                result.setValue(String.join(",", codeList));
            }
        } else if (StringUtil.isValidRegular(value, "^.{1,12}$")) {
            result.setType(2);
            result.setValue(value);
        } else {
            result.setType(3);
            result.setValue(value);
        }
        return result;
    }

    @GetMapping("transactions/list/v2")
    public ResponseEntity<List<TransactionRelatedResponseV2DTO>> getTransactionsMobile(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "value", defaultValue = "") String value,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate,
            @RequestParam(value = "type", defaultValue = "9") int type,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {
        List<TransactionRelatedV2DTO> dtos = new ArrayList<>();
        List<TransactionRelatedResponseV2DTO> result = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        HttpStatus httpStatus = null;
        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 4: terminal code
            // type = 5: status
            // type = 6: amount
            System.out.println("Pre get Transaction: " + System.currentTimeMillis());
            List<String> transType = new ArrayList<>();
            switch (type) {
                case 0:
                    transType.add("C");
                    break;
                case 1:
                    transType.add("D");
                    break;
                default:
                    transType.add("C");
                    transType.add("D");
                    break;
            }
            TypeValueFilterDTO typeCase = processFilterSearch(value, bankId);
            Callable<ResponseObjectDTO> callableTask3 = () -> {
                // Thực hiện công việc
                return getTerminalCodeAccess(bankId, userId);
            };
            Future<ResponseObjectDTO> future3 = executorService.submit(callableTask3);
            Callable<List<TransactionRelatedV2DTO>> callableTask1 = null;
            Callable<BankDetailTypeCaiValueDTO> callableTask2 = null;
            Future<List<TransactionRelatedV2DTO>> future1 = null;
            Future<BankDetailTypeCaiValueDTO> future2 = null;
            ResponseObjectDTO responseObjectDTO = future3.get();
            System.out.println("After get Transaction: " + System.currentTimeMillis());
            switch (responseObjectDTO.getStatus()) {
                case "SUCCESS":
                    callableTask1 = () -> {
                        List<TransactionRelatedV2DTO> relatedList = new ArrayList<>();
                        // Thực hiện công việc
                        String search = typeCase.getValue();
                        switch (typeCase.getType()) {
                            case 9:
                                // type = 9: all
                                // type = 1: reference_number
                                // type = 2: order_id
                                // type = 3: content
                                // type = 4: terminal code
                                // type = 5: status
                                // type = 6: amount
                                relatedList = transactionReceiveService.getTransactionsV2(bankId, transType, fromDate, toDate, offset);
                                break;
                            case 1:
                                relatedList = transactionReceiveService.getTransactionsV2ByFtCode(bankId, transType, search, fromDate, toDate, offset);
                                break;
                            case 2:
                                relatedList = transactionReceiveService.getTransactionsV2ByOrderId(bankId, transType, search, fromDate, toDate, offset);
                                break;
                            case 3:
                                relatedList = transactionReceiveService.getTransactionsV2ByContent(bankId, transType, search, fromDate, toDate, offset);
                                break;
                            case 4:
                                List<String> terminalCode = Arrays.stream(search.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
                                relatedList = transactionReceiveService.getTransactionsV2ByTerminalCode(bankId, transType, terminalCode, fromDate, toDate, offset);
                                break;
                            case 5:
                                relatedList = transactionReceiveService.getTransactionsV2ByStatus(bankId, transType, search, fromDate, toDate, offset);
                                break;
                            case 6:
                                relatedList = transactionReceiveService.getTransactionsV2ByAmount(bankId, transType, search, fromDate, toDate, offset);
                                break;
                        }
                        return relatedList;
                    };
                    future1 = executorService.submit(callableTask1);

                    callableTask2 = () -> {
                        // Thực hiện công việc
                        return accountBankReceiveService.getBankAccountTypeDetail(bankId);
                    };
                    future2 = executorService.submit(callableTask2);
                    break;
                case "CHECK":
                    List<String> listCode;
                    if (responseObjectDTO.getData() instanceof List) {
                        listCode = (List<String>) responseObjectDTO.getData();
                    } else {
                        listCode = new ArrayList<>();
                    }
                    callableTask1 = () -> {
                        List<TransactionRelatedV2DTO> relatedList = new ArrayList<>();
                        // Thực hiện công việc
                        String search = typeCase.getValue();
                        // type = 9: all
                        // type = 1: reference_number
                        // type = 2: order_id
                        // type = 3: content
                        // type = 4: terminal code
                        // type = 5: status
                        // type = 6: amount
                        switch (typeCase.getType()) {
                            case 9:
                                relatedList = transactionReceiveService.getTransactionsListCodeV2(bankId, listCode, transType, fromDate, toDate, offset);
                                break;
                            case 1:
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByReferenceNumber(bankId, listCode, transType, search, fromDate, toDate, offset);
                                break;
                            case 2:
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByOrderId(bankId, listCode, transType, search, fromDate, toDate, offset);
                                break;
                            case 3:
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByContent(bankId, listCode, transType, search, fromDate, toDate, offset);
                                break;
                            case 4:
                                List<String> terminalCode = Arrays.stream(search.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByTerminalCode(bankId, listCode, transType, terminalCode, fromDate, toDate, offset);
                                break;
                            case 5:
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByStatus(bankId, listCode, transType, search, fromDate, toDate, offset);
                                break;
                            case 6:
                                relatedList = transactionReceiveService
                                        .getTransactionsListCodeV2ByAmount(bankId, listCode, transType, search, fromDate, toDate, offset);
                                break;
                        }
                        return relatedList;
                    };
                    future1 = executorService.submit(callableTask1);

                    callableTask2 = () -> {
                        // Thực hiện công việc
                        return accountBankReceiveService.getBankAccountTypeDetail(bankId);
                    };
                    future2 = executorService.submit(callableTask2);
                    break;
                default:
                    // Thực hiện công việc
                    callableTask1 = ArrayList::new;
                    future1 = executorService.submit(callableTask1);

                    callableTask2 = () -> {
                        // Thực hiện công việc
                        return accountBankReceiveService.getBankAccountTypeDetail(bankId);
                    };
                    future2 = executorService.submit(callableTask2);
                    break;
            }

            BankDetailTypeCaiValueDTO bankDetailDTO = future2.get();
            dtos = future1.get();
            System.out.println("END get Transaction: " + System.currentTimeMillis());
            String caiValue = "";
            boolean isActiveService = true;
            if (Objects.nonNull(bankDetailDTO)) {
                caiValue = bankDetailDTO.getCaiValue();
                isActiveService = bankDetailDTO.getIsValidService();
            } else {
                caiValue = "";
            }
            String finalCaiValue = caiValue;
            if (isActiveService) {
                result = dtos.stream().map(dto -> {
                    TransactionRelatedResponseV2DTO responseDTO = new TransactionRelatedResponseV2DTO();
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                    responseDTO.setTransType(dto.getTransType());
                    responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                    responseDTO.setStatus(dto.getStatus());
                    responseDTO.setTime(dto.getTime());
                    responseDTO.setTimePaid(dto.getTimePaid());
                    responseDTO.setType(dto.getType());
                    responseDTO.setContent(dto.getContent());
                    responseDTO.setImgId(bankDetailDTO.getImgId());
                    responseDTO.setBankAccount(bankDetailDTO.getBankAccount());
                    responseDTO.setUserBankName(bankDetailDTO.getUserBankName());
                    responseDTO.setBankCode(bankDetailDTO.getBankCode());
                    responseDTO.setBankName(bankDetailDTO.getBankName());
                    responseDTO.setBankShortName(bankDetailDTO.getBankShortName());
                    if (StringUtil.isNullOrEmpty(dto.getQrCode()) && dto.getStatus() == 0) {
                        String qrCode = getQrCode(finalCaiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount());
                        responseDTO.setQrCode(qrCode);
                    } else {
                        responseDTO.setQrCode(dto.getQrCode());
                    }
                    return responseDTO;

                }).collect(Collectors.toList());
            } else {
                LocalDateTime now = LocalDateTime.now();
                long time = now.toEpochSecond(ZoneOffset.UTC);
                if (!dtos.isEmpty()) {
                    time = dtos.get(0).getTime();
                }
                SystemSettingEntity setting = systemSettingService.getSystemSetting();
                if (setting.getServiceActive() > time) {
                    result = dtos.stream().map(dto -> {
                        TransactionRelatedResponseV2DTO responseDTO = new TransactionRelatedResponseV2DTO();
                        responseDTO.setTransactionId(dto.getTransactionId());
                        responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                        responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                        responseDTO.setTransType(dto.getTransType());
                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                        responseDTO.setStatus(dto.getStatus());
                        responseDTO.setTime(dto.getTime());
                        responseDTO.setTimePaid(dto.getTimePaid());
                        responseDTO.setType(dto.getType());
                        responseDTO.setContent(dto.getContent());
                        responseDTO.setImgId(bankDetailDTO.getImgId());
                        responseDTO.setBankAccount(bankDetailDTO.getBankAccount());
                        responseDTO.setUserBankName(bankDetailDTO.getUserBankName());
                        responseDTO.setBankCode(bankDetailDTO.getBankCode());
                        responseDTO.setBankName(bankDetailDTO.getBankName());
                        responseDTO.setBankShortName(bankDetailDTO.getBankShortName());
                        if (StringUtil.isNullOrEmpty(dto.getQrCode()) && dto.getStatus() == 0) {
                            String qrCode = getQrCode(finalCaiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount());
                            responseDTO.setQrCode(qrCode);
                        } else {
                            responseDTO.setQrCode(dto.getQrCode());
                        }
                        return responseDTO;

                    }).collect(Collectors.toList());
                } else {
                    if (!dtos.isEmpty()) {
                        int lastIndex = dtos.size() - 1;
                        long lastTime = dtos.get(lastIndex).getTime();
                        TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(bankId);
                        if (entity != null) {
                            if (entity.getLastTimes() <= lastTime) {
                                result = dtos.stream().map(dto -> {
                                    TransactionRelatedResponseV2DTO responseDTO = new TransactionRelatedResponseV2DTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    responseDTO.setAmount("*****");
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setImgId(bankDetailDTO.getImgId());
                                    responseDTO.setBankAccount(bankDetailDTO.getBankAccount());
                                    responseDTO.setUserBankName(bankDetailDTO.getUserBankName());
                                    responseDTO.setBankCode(bankDetailDTO.getBankCode());
                                    responseDTO.setBankName(bankDetailDTO.getBankName());
                                    responseDTO.setBankShortName(bankDetailDTO.getBankShortName());
                                    if (StringUtil.isNullOrEmpty(dto.getQrCode()) && dto.getStatus() == 0) {
                                        String qrCode = getQrCode(finalCaiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount());
                                        responseDTO.setQrCode(qrCode);
                                    } else {
                                        responseDTO.setQrCode(dto.getQrCode());
                                    }
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            } else {
                                result = dtos.stream().map(dto -> {
                                    TransactionRelatedResponseV2DTO responseDTO = new TransactionRelatedResponseV2DTO();
                                    responseDTO.setTransactionId(dto.getTransactionId());
                                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                    responseDTO.setTransType(dto.getTransType());
                                    if (entity.getTransIds().contains(dto.getTransactionId())) {
                                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                    } else if (dto.getTime() < entity.getLastTimes()) {
                                        responseDTO.setAmount(formatAmountNumber(dto.getAmount()));
                                    } else {
                                        responseDTO.setAmount("*****");
                                    }
                                    responseDTO.setStatus(dto.getStatus());
                                    responseDTO.setTime(dto.getTime());
                                    responseDTO.setTimePaid(dto.getTimePaid());
                                    responseDTO.setType(dto.getType());
                                    responseDTO.setContent(dto.getContent());
                                    responseDTO.setImgId(bankDetailDTO.getImgId());
                                    responseDTO.setBankAccount(bankDetailDTO.getBankAccount());
                                    responseDTO.setUserBankName(bankDetailDTO.getUserBankName());
                                    responseDTO.setBankCode(bankDetailDTO.getBankCode());
                                    responseDTO.setBankName(bankDetailDTO.getBankName());
                                    responseDTO.setBankShortName(bankDetailDTO.getBankShortName());
                                    if (StringUtil.isNullOrEmpty(dto.getQrCode()) && dto.getStatus() == 0) {
                                        String qrCode = getQrCode(finalCaiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount());
                                        responseDTO.setQrCode(qrCode);
                                    } else {
                                        responseDTO.setQrCode(dto.getQrCode());
                                    }
                                    return responseDTO;

                                }).collect(Collectors.toList());
                            }
                        } else {
                            result = dtos.stream().map(dto -> {
                                TransactionRelatedResponseV2DTO responseDTO = new TransactionRelatedResponseV2DTO();
                                responseDTO.setTransactionId(dto.getTransactionId());
                                responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                                responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                                responseDTO.setTransType(dto.getTransType());
                                responseDTO.setAmount("*****");
                                responseDTO.setStatus(dto.getStatus());
                                responseDTO.setTime(dto.getTime());
                                responseDTO.setTimePaid(dto.getTimePaid());
                                responseDTO.setType(dto.getType());
                                responseDTO.setContent(dto.getContent());
                                responseDTO.setImgId(bankDetailDTO.getImgId());
                                responseDTO.setBankAccount(bankDetailDTO.getBankAccount());
                                responseDTO.setUserBankName(bankDetailDTO.getUserBankName());
                                responseDTO.setBankCode(bankDetailDTO.getBankCode());
                                responseDTO.setBankName(bankDetailDTO.getBankName());
                                responseDTO.setBankShortName(bankDetailDTO.getBankShortName());
                                if (StringUtil.isNullOrEmpty(dto.getQrCode()) && dto.getStatus() == 0) {
                                    String qrCode = getQrCode(finalCaiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount());
                                    responseDTO.setQrCode(qrCode);
                                } else {
                                    responseDTO.setQrCode(dto.getQrCode());
                                }
                                return responseDTO;

                            }).collect(Collectors.toList());
                        }

                    }
                }
            }
            System.out.println("Response Time: " + System.currentTimeMillis());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            executorService.shutdown();
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-extra/v2")
    public ResponseEntity<TransStatisticListExtra> getTransactionExtraByBankId(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        HttpStatus httpStatus = null;
        TransStatisticListExtra result = null;
        try {
            ResponseObjectDTO responseObjectDTO = getTerminalCodeAccess(bankId, userId);
            ITransStatisticListExtra extra = null;
            switch (responseObjectDTO.getStatus()) {
                case "SUCCESS":
                    extra = transactionReceiveService.getExtraTransactionsV2(bankId, fromDate, toDate);
                    break;
                case "CHECK":
                    List<String> listCode;
                    if (responseObjectDTO.getData() instanceof List) {
                        listCode = (List<String>) responseObjectDTO.getData();
                    } else {
                        listCode = new ArrayList<>();
                    }
                    extra = transactionReceiveService.getExtraTransactionsByListCodeV2(bankId, listCode, fromDate, toDate);
                    break;
            }
            if (Objects.nonNull(extra)) {
                result = new TransStatisticListExtra();
                result.setTotalCredit(extra.getTotalCredit());
                result.setTotalDebit(extra.getTotalDebit());
            } else {
                result = new TransStatisticListExtra();
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionExtraByBankId: ERROR: " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction/v2/{id}")
    public ResponseEntity<TransactionDetailResV2DTO> getTransactionDetailById(@PathVariable(value = "id") String id) {
        TransactionDetailResV2DTO result = null;
        TransactionDetailV2DTO dto = null;
        HttpStatus httpStatus = null;
        try {
            dto = transactionReceiveService.getTransactionV2ById(id);
            if (Objects.nonNull(dto)) {
                String caiValue = caiBankService.getCaiValueByBankId(dto.getBankId());

                boolean isActiveService = accountBankReceiveService.checkIsActiveService(dto.getBankId());
                if (isActiveService) {
                    result = new TransactionDetailResV2DTO();
                    result.setId(dto.getId());
                    result.setBankId(dto.getBankId());
                    result.setImgId(dto.getImgId());
                    result.setReferenceNumber(StringUtil.getValueNullChecker(dto.getReferenceNumber()));
                    result.setBankAccount(dto.getBankAccount());
                    result.setUserBankName(dto.getUserBankName());
                    result.setBankShortName(StringUtil.getValueNullChecker(dto.getBankShortName()));
                    result.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                    result.setTransType(dto.getTransType());
                    result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                    result.setStatus(dto.getStatus());
                    result.setTime(dto.getTime());
                    result.setTimePaid(dto.getTimePaid());
                    result.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                    result.setContent(dto.getContent());
                    result.setType(dto.getType());
                    result.setBankCode(dto.getBankCode());
                    result.setBankName(dto.getBankName());
                    result.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                    result.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                    result.setHashTag(StringUtil.getValueNullChecker(dto.getHashTag()));
                    if (StringUtil.isNullOrEmpty(dto.getQrCode())) {
                        String qrCode = getQrCode(caiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount() + "");
                        result.setQrCode(qrCode);
                    } else {
                        result.setQrCode(StringUtil.getValueNullChecker(dto.getQrCode()));
                    }
                } else {
                    long time = dto.getTime();
                    SystemSettingEntity setting = systemSettingService.getSystemSetting();
                    if (setting.getServiceActive() > time) {
                        result = new TransactionDetailResV2DTO();
                        result.setId(dto.getId());
                        result.setBankId(dto.getBankId());
                        result.setImgId(dto.getImgId());
                        result.setReferenceNumber(StringUtil.getValueNullChecker(dto.getReferenceNumber()));
                        result.setBankAccount(dto.getBankAccount());
                        result.setUserBankName(dto.getUserBankName());
                        result.setBankShortName(StringUtil.getValueNullChecker(dto.getBankShortName()));
                        result.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                        result.setTransType(dto.getTransType());
                        result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                        result.setStatus(dto.getStatus());
                        result.setTime(dto.getTime());
                        result.setTimePaid(dto.getTimePaid());
                        result.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                        result.setContent(dto.getContent());
                        result.setType(dto.getType());
                        result.setBankCode(dto.getBankCode());
                        result.setBankName(dto.getBankName());
                        result.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                        result.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                        result.setHashTag(StringUtil.getValueNullChecker(dto.getHashTag()));
                        if (StringUtil.isNullOrEmpty(dto.getQrCode())) {
                            String qrCode = getQrCode(caiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount() + "");
                            result.setQrCode(qrCode);
                        } else {
                            result.setQrCode(StringUtil.getValueNullChecker(dto.getQrCode()));
                        }
                    } else {
                        long lastTime = dto.getTime();
                        TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(dto.getBankId());
                        if (entity != null) {
                            if (entity.getLastTimes() <= lastTime) {
                                result = new TransactionDetailResV2DTO();
                                result.setId(dto.getId());
                                result.setBankId(dto.getBankId());
                                result.setImgId(dto.getImgId());
                                result.setReferenceNumber(StringUtil.getValueNullChecker(dto.getReferenceNumber()));
                                result.setBankAccount(dto.getBankAccount());
                                result.setUserBankName(dto.getUserBankName());
                                result.setBankShortName(StringUtil.getValueNullChecker(dto.getBankShortName()));
                                result.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                                result.setTransType(dto.getTransType());
                                result.setAmount("*****");
                                result.setStatus(dto.getStatus());
                                result.setTime(dto.getTime());
                                result.setTimePaid(dto.getTimePaid());
                                result.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                                result.setContent(dto.getContent());
                                result.setType(dto.getType());
                                result.setBankCode(dto.getBankCode());
                                result.setBankName(dto.getBankName());
                                result.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                                result.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                result.setHashTag(StringUtil.getValueNullChecker(dto.getHashTag()));
                                if (StringUtil.isNullOrEmpty(dto.getQrCode())) {
                                    String qrCode = getQrCode(caiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount() + "");
                                    result.setQrCode(qrCode);
                                } else {
                                    result.setQrCode(StringUtil.getValueNullChecker(dto.getQrCode()));
                                }
                            } else {
                                result = new TransactionDetailResV2DTO();
                                result.setId(dto.getId());
                                result.setBankId(dto.getBankId());
                                result.setImgId(dto.getImgId());
                                result.setReferenceNumber(StringUtil.getValueNullChecker(dto.getReferenceNumber()));
                                result.setBankAccount(dto.getBankAccount());
                                result.setUserBankName(dto.getUserBankName());
                                result.setBankShortName(StringUtil.getValueNullChecker(dto.getBankShortName()));
                                result.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                                result.setTransType(dto.getTransType());
                                if (entity.getTransIds().contains(dto.getId())) {
                                    result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                                } else if (dto.getTime() < entity.getLastTimes()) {
                                    result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                                } else {
                                    result.setAmount("*****");
                                }
                                result.setStatus(dto.getStatus());
                                result.setTime(dto.getTime());
                                result.setTimePaid(dto.getTimePaid());
                                result.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                                result.setContent(dto.getContent());
                                result.setType(dto.getType());
                                result.setBankCode(dto.getBankCode());
                                result.setBankName(dto.getBankName());
                                result.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                                result.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                result.setHashTag(StringUtil.getValueNullChecker(dto.getHashTag()));
                                if (StringUtil.isNullOrEmpty(dto.getQrCode())) {
                                    String qrCode = getQrCode(caiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount() + "");
                                    result.setQrCode(qrCode);
                                } else {
                                    result.setQrCode(StringUtil.getValueNullChecker(dto.getQrCode()));
                                }
                            }
                        } else {
                            result = new TransactionDetailResV2DTO();
                            result.setId(dto.getId());
                            result.setBankId(dto.getBankId());
                            result.setImgId(dto.getImgId());
                            result.setReferenceNumber(StringUtil.getValueNullChecker(dto.getReferenceNumber()));
                            result.setBankAccount(dto.getBankAccount());
                            result.setUserBankName(dto.getUserBankName());
                            result.setBankShortName(StringUtil.getValueNullChecker(dto.getBankShortName()));
                            result.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                            result.setTransType(dto.getTransType());
                            result.setAmount("*****");
                            result.setStatus(dto.getStatus());
                            result.setTime(dto.getTime());
                            result.setTimePaid(dto.getTimePaid());
                            result.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                            result.setContent(dto.getContent());
                            result.setType(dto.getType());
                            result.setBankCode(dto.getBankCode());
                            result.setBankName(dto.getBankName());
                            result.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                            result.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                            result.setHashTag(StringUtil.getValueNullChecker(dto.getHashTag()));
                            if (StringUtil.isNullOrEmpty(dto.getQrCode())) {
                                String qrCode = getQrCode(caiValue, dto.getBankAccount(), dto.getContent(), dto.getAmount() + "");
                                result.setQrCode(qrCode);
                            } else {
                                result.setQrCode(StringUtil.getValueNullChecker(dto.getQrCode()));
                            }
                        }

                    }
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-log/v2/{transactionId}")
    public ResponseEntity<List<TransactionReceiveLogDTO>> getTransactionLogById(
            @PathVariable(value = "transactionId") String transactionId) {
        List<TransactionReceiveLogDTO> result = null;
        List<ITransactionReceiveLogDTO> dtos = null;
        HttpStatus httpStatus = null;
        try {
            dtos = transactionReceiveLogService.getTransactionLogsByTransId(transactionId);
            if (Objects.nonNull(dtos)) {
                result = dtos.stream().map(dto -> {
                    TransactionReceiveLogDTO responseDTO = new TransactionReceiveLogDTO();
                    responseDTO.setId(dto.getId());
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setMessage(StringUtil.getValueNullChecker(dto.getMessage()));
                    responseDTO.setStatus(StringUtil.getValueNullChecker(dto.getStatus()));
                    responseDTO.setStatusCode(StringUtil.getValueNullChecker(dto.getStatusCode()));
                    responseDTO.setType(StringUtil.getValueNullChecker(dto.getType()));
                    responseDTO.setTimeRequest(StringUtil.getValueNullChecker(dto.getTimeRequest()));
                    responseDTO.setTimeResponse(StringUtil.getValueNullChecker(dto.getTimeResponse()));
                    return responseDTO;
                }).collect(Collectors.toList());
            } else {
                result = new ArrayList<>();
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getQrCode(String caiValue, String bankAccount, String content, String amount) {
        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
        vietQRGenerateDTO.setCaiValue(caiValue);
        vietQRGenerateDTO.setBankAccount(bankAccount);
        vietQRGenerateDTO.setAmount(amount);
        vietQRGenerateDTO.setContent(content);
        return VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
    }

    @PostMapping("transaction/hash-tag")
    public ResponseEntity<ResponseMessageDTO> updateHashTagTransaction(@Valid @RequestBody TransactionHashTagDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            transactionReceiveService.updateHashTagTransaction(dto.getHashTag(), dto.getTransactionId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
            logger.error("uploadImageTransaction: ERROR: " + e.toString());
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction/{id}")
    public ResponseEntity<TransactionDetailResDTO> getTransactionById(@PathVariable(value = "id") String id) {
        TransactionDetailResDTO result = null;
        TransactionDetailDTO dto = null;
        HttpStatus httpStatus = null;
        try {
            dto = transactionReceiveService.getTransactionById(id);
            boolean isActiveService = accountBankReceiveService.checkIsActiveService(dto.getBankId());
            if (isActiveService) {
                result = new TransactionDetailResDTO();
                result.setId(dto.getId());
                result.setBankId(dto.getBankId());
                result.setRefId(dto.getRefId());
                result.setTraceId(dto.getTraceId());
                result.setBankAccountName(dto.getBankAccountName());
                result.setBankCode(dto.getBankCode());
                result.setBankName(dto.getBankName());
                result.setImgId(dto.getImgId());
                result.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                result.setBankAccount(dto.getBankAccount());
                result.setBankShortName(dto.getBankShortName() != null ? dto.getBankShortName() : "");
                result.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                result.setTransType(dto.getTransType());
                result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                result.setStatus(dto.getStatus());
                result.setTime(dto.getTime());
                result.setTimePaid(dto.getTimePaid());
                result.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                result.setContent(dto.getContent());
                result.setType(dto.getType());
                result.setNote(dto.getNote() != null ? dto.getNote() : "");
            } else {
                long time = dto.getTime();
                SystemSettingEntity setting = systemSettingService.getSystemSetting();
                if (setting.getServiceActive() > time) {
                    result = new TransactionDetailResDTO();
                    result.setId(dto.getId());
                    result.setBankId(dto.getBankId());
                    result.setRefId(dto.getRefId());
                    result.setTraceId(dto.getTraceId());
                    result.setBankAccountName(dto.getBankAccountName());
                    result.setBankCode(dto.getBankCode());
                    result.setBankName(dto.getBankName());
                    result.setImgId(dto.getImgId());
                    result.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                    result.setBankAccount(dto.getBankAccount());
                    result.setBankShortName(dto.getBankShortName() != null ? dto.getBankShortName() : "");
                    result.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                    result.setTransType(dto.getTransType());
                    result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                    result.setStatus(dto.getStatus());
                    result.setTime(dto.getTime());
                    result.setTimePaid(dto.getTimePaid());
                    result.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                    result.setContent(dto.getContent());
                    result.setType(dto.getType());
                    result.setNote(dto.getNote() != null ? dto.getNote() : "");
                } else {
                    long lastTime = dto.getTime();
                    TransReceiveTempEntity entity = transReceiveTempService.getLastTimeByBankId(dto.getBankId());
                    if (entity != null) {
                        if (entity.getLastTimes() <= lastTime) {
                            result = new TransactionDetailResDTO();
                            result.setId(dto.getId());
                            result.setBankId(dto.getBankId());
                            result.setRefId(dto.getRefId());
                            result.setTraceId(dto.getTraceId());
                            result.setBankAccountName(dto.getBankAccountName());
                            result.setBankCode(dto.getBankCode());
                            result.setBankName(dto.getBankName());
                            result.setImgId(dto.getImgId());
                            result.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                            result.setBankAccount(dto.getBankAccount());
                            result.setBankShortName(dto.getBankShortName() != null ? dto.getBankShortName() : "");
                            result.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                            result.setTransType(dto.getTransType());
                            result.setAmount("*****");
                            result.setStatus(dto.getStatus());
                            result.setTime(dto.getTime());
                            result.setTimePaid(dto.getTimePaid());
                            result.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                            result.setContent(dto.getContent());
                            result.setType(dto.getType());
                            result.setNote(dto.getNote() != null ? dto.getNote() : "");
                        } else {
                            result = new TransactionDetailResDTO();
                            result.setId(dto.getId());
                            result.setBankId(dto.getBankId());
                            result.setRefId(dto.getRefId());
                            result.setTraceId(dto.getTraceId());
                            result.setBankAccountName(dto.getBankAccountName());
                            result.setBankCode(dto.getBankCode());
                            result.setBankName(dto.getBankName());
                            result.setImgId(dto.getImgId());
                            result.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                            result.setBankAccount(dto.getBankAccount());
                            result.setBankShortName(dto.getBankShortName() != null ? dto.getBankShortName() : "");
                            result.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                            result.setTransType(dto.getTransType());
                            if (entity.getTransIds().contains(dto.getId())) {
                                result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                            } else if (dto.getTime() < entity.getLastTimes()) {
                                result.setAmount(formatAmountNumber(dto.getAmount() + ""));
                            } else {
                                result.setAmount("*****");
                            }
                            result.setStatus(dto.getStatus());
                            result.setTime(dto.getTime());
                            result.setTimePaid(dto.getTimePaid());
                            result.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                            result.setContent(dto.getContent());
                            result.setType(dto.getType());
                            result.setNote(dto.getNote() != null ? dto.getNote() : "");
                        }
                    } else {
                        result = new TransactionDetailResDTO();
                        result.setId(dto.getId());
                        result.setBankId(dto.getBankId());
                        result.setRefId(dto.getRefId());
                        result.setTraceId(dto.getTraceId());
                        result.setBankAccountName(dto.getBankAccountName());
                        result.setBankCode(dto.getBankCode());
                        result.setBankName(dto.getBankName());
                        result.setImgId(dto.getImgId());
                        result.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                        result.setBankAccount(dto.getBankAccount());
                        result.setBankShortName(dto.getBankShortName() != null ? dto.getBankShortName() : "");
                        result.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                        result.setTransType(dto.getTransType());
                        result.setAmount("*****");
                        result.setStatus(dto.getStatus());
                        result.setTime(dto.getTime());
                        result.setTimePaid(dto.getTimePaid());
                        result.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                        result.setContent(dto.getContent());
                        result.setType(dto.getType());
                        result.setNote(dto.getNote() != null ? dto.getNote() : "");
                    }

                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction/image")
    public ResponseEntity<ResponseMessageDTO> uploadImageTransaction(@Valid @RequestParam String transactionId,
                                                                     @Valid @RequestParam MultipartFile image) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            UUID uuid2 = UUID.randomUUID();
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            ImageEntity imageEntity = new ImageEntity(uuid.toString(), fileName, image.getBytes());
            imageService.insertImage(imageEntity);
            TransactionReceiveImageEntity transactionReceiveImageEntity = new TransactionReceiveImageEntity();
            transactionReceiveImageEntity.setId(uuid2.toString());
            transactionReceiveImageEntity.setImgId(uuid.toString());
            transactionReceiveImageEntity.setTransactionReceiveId(transactionId);
            int check = transactionReceiveImageService.insertTransactionReceiveImage(transactionReceiveImageEntity);
            if (check == 1) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
            logger.error("uploadImageTransaction: ERROR: " + e.toString());
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get transaction image
    @GetMapping("transaction/image/{transactionId}")
    public ResponseEntity<List<TransImgIdDTO>> getTransactionImages(
            @PathVariable(value = "transactionId") String transactionId) {
        List<TransImgIdDTO> result = null;
        HttpStatus httpStatus = null;
        try {
            result = new ArrayList<>();
            List<String> imgIds = transactionReceiveImageService.getImgIdsByTransReceiveId(transactionId);
            if (imgIds != null && !imgIds.isEmpty()) {
                for (String imgId : imgIds) {
                    TransImgIdDTO dto = new TransImgIdDTO(imgId);
                    result.add(dto);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("getTransactionImages: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get transactions for customer-sync; max 3 months data
    @PostMapping("transactions")
    public ResponseEntity<Object> getTransactionsCheck(@Valid @RequestBody TransactionDateDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            boolean isError = false;
            String fromDateStr = dto.getFromDate();
            String toDateStr = dto.getToDate();
            LocalDate fromDate = parseDate(fromDateStr);
            if (fromDate == null) {
                isError = true;
                result = new ResponseMessageDTO("ERROR", "Invalid fromDate format. Expected format is yyyy-MM-dd.");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            LocalDate toDate = parseDate(toDateStr);
            if (toDate == null) {
                isError = true;
                result = new ResponseMessageDTO("ERROR", "Invalid toDate format. Expected format is yyyy-MM-dd.");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                isError = true;
                return new ResponseEntity<>("fromDate cannot be after toDate.", HttpStatus.BAD_REQUEST);
            }
            if (fromDate != null && toDate != null) {
                LocalDate fromDatePlus3Months = fromDate.plusMonths(3);
                if (fromDatePlus3Months.isBefore(toDate) || fromDatePlus3Months.isEqual(toDate)) {
                    isError = true;
                    result = new ResponseMessageDTO("ERROR",
                            "The difference between fromDate and toDate cannot be greater than 3 months.");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
            // Add more checks here if needed
            if (!isError) {
                List<TransactionCheckDTO> list = new ArrayList<>();
                list = transactionBankService.getTransactionsCheck(fromDateStr, toDateStr, dto.getBankAccount());
                result = list;
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("getTransactionsCheck: ERROR: " + e.toString());
            result = new ResponseMessageDTO("ERROR",
                    "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // check transaction
    // TransactionCheckStatusDTO
    @GetMapping("transaction/check/{transactionId}")
    public ResponseEntity<TransactionCheckStatusDTO> checkTransactionStatus(@PathVariable String transactionId) {
        TransactionCheckStatusDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (transactionId != null) {
                result = transactionReceiveService.getTransactionCheckStatus(transactionId);
                if (result != null) {
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("checkTransactionStatus: RECORD NULL");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("checkTransactionStatus: TRANSACTION ID NULL");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("checkTransactionStatus: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // synchronize transaction from RPA
    @PostMapping("transaction/rpa-sync")
    public ResponseEntity<ResponseMessageDTO> syncTransactionRpa(@RequestBody TransReceiveRpaDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<TransactionRPAEntity> transactions = new ArrayList<>();
            if (dto != null) {
                String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                if (bankTypeId != null) {
                    AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                            .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
                    if (accountBankEntity != null) {
                        //
                        Boolean rpaContainId = bankTypeService.getRpaContainIdByBankCode(dto.getBankCode());
                        if (dto.getTransactions() != null && !dto.getTransactions().isEmpty()) {
                            for (TransSyncRpaDTO transSyncRpaDTO : dto.getTransactions()) {
                                // check duplication transaction by transaction id ->getReferenceNumber
                                List<String> checkDuplicatedReferenceNumber = transactionRPAService
                                        .checkExistedTransaction(transSyncRpaDTO.getReferenceNumber());
                                if (rpaContainId != null && rpaContainId == true) {
                                    if (checkDuplicatedReferenceNumber == null
                                            || checkDuplicatedReferenceNumber.isEmpty()) {
                                        TransactionRPAEntity entity = new TransactionRPAEntity();
                                        UUID uuid = UUID.randomUUID();
                                        entity.setId(uuid.toString());
                                        entity.setAmount(transSyncRpaDTO.getAmount());
                                        entity.setBankAccount(dto.getBankAccount());
                                        entity.setBankId(accountBankEntity.getId());
                                        entity.setContent(transSyncRpaDTO.getContent());
                                        entity.setRefId(transSyncRpaDTO.getId());
                                        entity.setStatus(1);
                                        entity.setTime(convertTimeStringToInteger(transSyncRpaDTO.getTime()));
                                        entity.setTimePaid(convertTimeStringToInteger(transSyncRpaDTO.getTime()));
                                        entity.setTransType(transSyncRpaDTO.getTransType());
                                        entity.setReferenceNumber(transSyncRpaDTO.getReferenceNumber());
                                        transactions.add(entity);
                                    }
                                } else {
                                    TransactionRPAEntity entity = new TransactionRPAEntity();
                                    UUID uuid = UUID.randomUUID();
                                    entity.setId(uuid.toString());
                                    entity.setAmount(transSyncRpaDTO.getAmount());
                                    entity.setBankAccount(dto.getBankAccount());
                                    entity.setBankId(accountBankEntity.getId());
                                    entity.setContent(transSyncRpaDTO.getContent());
                                    entity.setRefId(transSyncRpaDTO.getId());
                                    entity.setStatus(1);
                                    entity.setTime(convertTimeStringToInteger(transSyncRpaDTO.getTime()));
                                    entity.setTimePaid(convertTimeStringToInteger(transSyncRpaDTO.getTime()));
                                    entity.setTransType(transSyncRpaDTO.getTransType());
                                    entity.setReferenceNumber(transSyncRpaDTO.getReferenceNumber());
                                    transactions.add(entity);
                                }

                            }
                            System.out.println("transactions size: " + transactions.size());
                            if (transactions != null && !transactions.isEmpty()) {
                                int check = transactionRPAService.insertAllTransactionRPA(transactions);
                                System.out.println("check " + check);
                                if (check == 1) {
                                    result = new ResponseMessageDTO("SUCCESS", "");
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    logger.error("syncTransactionRpa: INSERT TRANSACTIONS FAILED");
                                    result = new ResponseMessageDTO("FAILED", "E05");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            }
                        } else {
                            logger.error("syncTransactionRpa: TRANSACTION LIST IS INVALID");
                            result = new ResponseMessageDTO("FAILED", "E53");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error("syncTransactionRpa: BANK ACCOUNT IS NOT EXISTED");
                        result = new ResponseMessageDTO("FAILED", "E52");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("syncTransactionRpa: NOT FOUND BANK TYPE ID");
                    result = new ResponseMessageDTO("FAILED", "E51");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }

            } else {
                logger.error("syncTransactionRpa: RPA SYSTEM ERROR");
                result = new ResponseMessageDTO("FAILED", "E54");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("syncTransactionRpa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("transaction/overview/{bankId}")
    public ResponseEntity<TransStatisticResponseDTO> getTransactionOverview(
            @PathVariable("bankId") String bankId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "month") String month) {
        TransStatisticResponseDTO result = null;
        TransStatisticDTO dto = null;
        HttpStatus httpStatus = null;
        try {
            String checkIsOwner = accountBankReceiveService.checkIsOwner(bankId, userId);
            if (!StringUtil.isNullOrEmpty(checkIsOwner) && StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverview(bankId, month);
            } else {
                if (StringUtil.isNullOrEmpty(terminalCode)) {
                    dto = transactionReceiveService.getTransactionOverview(bankId, month, userId);
                } else {
                    dto = transactionReceiveService.getTransactionOverview(bankId, terminalCode, month, userId);
                }
            }
            if (dto != null && Objects.nonNull(dto.getTotalCashIn()) && Objects.nonNull(dto.getTotalCashOut())) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;

            } else if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverviewNotSync(bankId, terminalCode, month);
                if (dto != null) {
                    result = new TransStatisticResponseDTO();
                    result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                    result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                    result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                    result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                    result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                    httpStatus = HttpStatus.OK;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (dto != null) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at getBankOverview: " + e.toString());
            logger.error("Error at getBankOverview: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("transaction/overview-by-day/{bankId}")
    public ResponseEntity<TransStatisticResponseDTO> getTransactionOverview(
            @PathVariable("bankId") String bankId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) {
        TransStatisticResponseDTO result = null;
        TransStatisticDTO dto = null;
        HttpStatus httpStatus = null;
        try {
            String checkIsOwner = accountBankReceiveService.checkIsOwner(bankId, userId);
            if (!StringUtil.isNullOrEmpty(checkIsOwner) && StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverviewByDay(bankId, fromDate, toDate);
            } else {
                if (StringUtil.isNullOrEmpty(terminalCode)) {
                    dto = transactionReceiveService.getTransactionOverviewByDay(bankId, fromDate, toDate, userId);
                } else {
                    dto = transactionReceiveService.getTransactionOverviewByDay(bankId, terminalCode, fromDate, toDate, userId);
                }
            }
            if (dto != null && Objects.nonNull(dto.getTotalCashIn()) && Objects.nonNull(dto.getTotalCashOut())) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;

            } else if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverviewNotSync(bankId, terminalCode, fromDate, toDate);
                if (dto != null) {
                    result = new TransStatisticResponseDTO();
                    result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                    result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                    result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                    result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                    result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                    httpStatus = HttpStatus.OK;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (dto != null) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn() != null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut() != null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC() != null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD() != null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans() != null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at getBankOverview: " + e.toString());
            logger.error("Error at getBankOverview: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("transaction/statistic-by-date")
    public ResponseEntity<List<TransStatisticByTimeDTO>> getTransactionStatisticByDate(
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate,
            @RequestParam(value = "userId") String userId) {
        List<TransStatisticByTimeDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            String checkIsOwner = accountBankReceiveService.checkIsOwner(bankId, userId);
            if (!StringUtil.isNullOrEmpty(checkIsOwner) && StringUtil.isNullOrEmpty(terminalCode)) {
                List<TransStatisticByTimeDTO> transactions
                        = transactionReceiveService.getTransStatisticByBankIdAndDate(bankId, fromDate, toDate);
                result = transactions;
                httpStatus = HttpStatus.OK;
            } else {
                if (StringUtil.isNullOrEmpty(terminalCode)) {
                    List<TransStatisticByTimeDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalIdAndDate(bankId, fromDate, toDate, userId);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                } else {
                    List<TransStatisticByTimeDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalIdAndDate(bankId, terminalCode, fromDate, toDate, userId);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                }
            }
            // if result is empty and is_owner = true and terminalCode is not null
            if (result == null || result.isEmpty()) {
                httpStatus = null;
                if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                    List<TransStatisticByTimeDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalIdNotSync(bankId, terminalCode, fromDate, toDate);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ArrayList<>();
                    httpStatus = HttpStatus.OK;
                }
            }

        } catch (Exception e) {
            System.out.println("Error at getTransactionStatistic: " + e.toString());
            logger.error("Error at getTransactionStatistic: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("transaction/statistic")
    public ResponseEntity<List<TransStatisticByDateDTO>> getTransactionStatistic(
            @RequestParam(value = "terminalCode") String terminalCode,
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "month") String month,
            @RequestParam(value = "userId") String userId) {
        List<TransStatisticByDateDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            String checkIsOwner = accountBankReceiveService.checkIsOwner(bankId, userId);
            if (!StringUtil.isNullOrEmpty(checkIsOwner) && StringUtil.isNullOrEmpty(terminalCode)) {
                List<TransStatisticByDateDTO> transactions
                        = transactionReceiveService.getTransStatisticByBankId(bankId, month);
                result = transactions;
                httpStatus = HttpStatus.OK;
            } else {
                if (StringUtil.isNullOrEmpty(terminalCode)) {
                    List<TransStatisticByDateDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalId(bankId, month, userId);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                } else {
                    List<TransStatisticByDateDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalId(bankId, terminalCode, month, userId);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                }
            }
            // if result is empty and is_owner = true and terminalCode is not null
            if (result == null || result.isEmpty()) {
                httpStatus = null;
                if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                    List<TransStatisticByDateDTO> transactions
                            = transactionReceiveService.getTransStatisticByTerminalIdNotSync(bankId, terminalCode, month);
                    result = transactions;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ArrayList<>();
                    httpStatus = HttpStatus.OK;
                }
            }

        } catch (Exception e) {
            System.out.println("Error at getTransactionStatistic: " + e.toString());
            logger.error("Error at getTransactionStatistic: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public static long convertTimeStringToInteger(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        long result = 0;
        try {
            Date date = format.parse(timeString);
            long timestamp = date.getTime() / 1000; // Chia cho 1000 để chuyển đổi sang giây
            result = (long) timestamp;
        } catch (Exception e) {
            System.out.println("convertTimeStringToInteger: ERORR: " + e.toString());
            logger.error("convertTimeStringToInteger: ERORR: " + e.toString());
        }
        return result;
    }

    @GetMapping("admin/transactions/customer-sync")
    public ResponseEntity<List<TransByCusSyncDTO>> getTransactionsByCustomerSync(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "customerSyncId") String customerSyncId,
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "from", required = false) String fromDate,
            @RequestParam(value = "to", required = false) String toDate) {
        List<TransByCusSyncDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate)) {
                result = transactionReceiveService.getTransactionsByCustomerSync(bankId, customerSyncId, offset);
            } else {
                result = transactionReceiveService.getTransactionsByCustomerSync(bankId, customerSyncId, offset, fromDate,
                        toDate);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsByCustomerSync: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("admin/transactions/statistic")
    public ResponseEntity<TransStatisticDTO> getTransStatisticAdmin(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "customerSyncId") String customerSyncId,
            @RequestParam(value = "month") String month) {
        TransStatisticDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // type = 9 => all
            // type = 0 => month
            if (type == 0) {
                result = transactionReceiveService.getTransStatisticCustomerSyncByMonth(customerSyncId, month);
                httpStatus = HttpStatus.OK;
            } else if (type == 9) {
                result = transactionReceiveService.getTransStatisticCustomerSync(customerSyncId);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getTransactionsByCustomerSync: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    ///
    // API check transaction by merchant token
    @PostMapping("transactions/check-order")
    public ResponseEntity<Object> checkTransactionStatus(
            @RequestHeader("Authorization") String token,
            @RequestBody TransactionCheckOrderInputDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // 1. get info from token
            String username = getUsernameFromToken(token);
            if (username != null && !username.trim().isEmpty()) {
                // 2. If valid token, check valid object
                if (dto != null) {
                    // 3. If valid object, check condition (bank&trans belong to merchant)
                    List<String> checkExistedCustomerSync = accountCustomerBankService
                            .checkExistedCustomerSyncByUsername(username);
                    if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
                        // 4. Check checksum
                        String checkSum = BankEncryptUtil.generateMD5CheckOrderChecksum(dto.getBankAccount(), username);
                        if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                            // 5. Find transaction
                            // 0: get by orderId
                            // 1: get by referenceNumber
                            List<TransReceiveResponseDTO> responseDTOs = new ArrayList<>();
                            List<TransReceiveResponseCheckOrderDTO> response = new ArrayList<>();
                            List<IRefundCheckOrderDTO> iRefundCheckOrderDTOS = new ArrayList<>();
                            if (dto.getValue() != null && !dto.getValue().trim().isEmpty()) {
                                if (dto.getType() != null && dto.getType() == 0) {
                                    responseDTOs = transactionReceiveService.getTransByOrderId(dto.getValue(),
                                            dto.getBankAccount());
                                    if (responseDTOs != null && !responseDTOs.isEmpty()) {
                                        iRefundCheckOrderDTOS = transactionRefundService
                                                .getTotalRefundedByTransactionId(responseDTOs.stream()
                                                        .map(TransReceiveResponseDTO::getTransactionId)
                                                        .collect(Collectors.toList()));
                                        Map<String, RefundCheckOrderDTO> refundCheckOrderDTOMap;
                                        if (iRefundCheckOrderDTOS != null && !iRefundCheckOrderDTOS.isEmpty()) {
                                            refundCheckOrderDTOMap = iRefundCheckOrderDTOS.stream()
                                                    .collect(Collectors.toMap(IRefundCheckOrderDTO::getTransactionId, item ->
                                                            new RefundCheckOrderDTO(item.getTransactionId(), item.getRefundCount(),
                                                                    item.getAmountRefunded())));
                                        } else {
                                            refundCheckOrderDTOMap = new HashMap<>();
                                        }

                                        boolean allTransTypeD = responseDTOs.stream().allMatch(item -> "D".equals(item.getTransType()));
                                        if (allTransTypeD) {
                                            List<CheckOrderTransTypeDDTO> responseD = responseDTOs.stream().map(item -> {
                                                CheckOrderTransTypeDDTO checkOrderDTO = new CheckOrderTransTypeDDTO();
                                                checkOrderDTO.setAmount(item.getAmount());
                                                checkOrderDTO.setStatus(item.getStatus());
                                                checkOrderDTO.setNote(StringUtil.getValueNullChecker(item.getNote()));
                                                checkOrderDTO.setContent(item.getContent());
                                                checkOrderDTO.setOrderId(item.getOrderId());
                                                checkOrderDTO.setReferenceNumber(item.getReferenceNumber());
                                                checkOrderDTO.setTerminalCode(StringUtil.getValueNullChecker(item.getTerminalCode()));
                                                checkOrderDTO.setTimeCreated(item.getTimeCreated());
                                                checkOrderDTO.setTimePaid(item.getTimePaid());
                                                String checkExistRefundReferenceNumber =
                                                        transactionRefundService.checkExistRefundTransaction(dto.getBankAccount(),
                                                                item.getReferenceNumber());
                                                if (StringUtil.isNullOrEmpty(checkExistRefundReferenceNumber)) {
                                                    checkOrderDTO.setType(item.getType());
                                                } else {
                                                    checkOrderDTO.setType(6);
                                                }
                                                checkOrderDTO.setTransType(item.getTransType());
                                                return checkOrderDTO;
                                            }).collect(Collectors.toList());
                                            result = responseD;
                                        } else {
                                            response = responseDTOs.stream().map(item -> {
                                                TransReceiveResponseCheckOrderDTO checkOrderDTO = new TransReceiveResponseCheckOrderDTO();
                                                checkOrderDTO.setAmount(item.getAmount());
                                                checkOrderDTO.setStatus(item.getStatus());
                                                checkOrderDTO.setNote(StringUtil.getValueNullChecker(item.getNote()));
                                                checkOrderDTO.setContent(item.getContent());
                                                checkOrderDTO.setOrderId(item.getOrderId());
                                                checkOrderDTO.setReferenceNumber(item.getReferenceNumber());
                                                checkOrderDTO.setTerminalCode(StringUtil.getValueNullChecker(item.getTerminalCode()));
                                                checkOrderDTO.setTimeCreated(item.getTimeCreated());
                                                checkOrderDTO.setTimePaid(item.getTimePaid());
                                                checkOrderDTO.setType(item.getType());
                                                checkOrderDTO.setTransType(item.getTransType());
                                                RefundCheckOrderDTO refundCheckOrderDTO = refundCheckOrderDTOMap
                                                        .getOrDefault(item.getTransactionId(), new RefundCheckOrderDTO(item.getTransactionId()));
                                                checkOrderDTO.setRefundCount(refundCheckOrderDTO.getRefundCount());
                                                checkOrderDTO.setAmountRefunded(refundCheckOrderDTO.getAmountRefunded());
                                                return checkOrderDTO;
                                            }).collect(Collectors.toList());
                                            result = response;
                                        }
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        logger.error("checkTransactionStatus: NOT FOUND TRANSACTION");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E96");
                                    }
                                } else if (dto.getType() != null && dto.getType() == 1) {
                                    responseDTOs = transactionReceiveService.getTransByReferenceNumber(dto.getValue(),
                                            dto.getBankAccount());
                                    if (responseDTOs != null && !responseDTOs.isEmpty()) {
                                        iRefundCheckOrderDTOS = transactionRefundService
                                                .getTotalRefundedByTransactionId(responseDTOs.stream()
                                                        .map(TransReceiveResponseDTO::getTransactionId)
                                                        .collect(Collectors.toList()));
                                        Map<String, RefundCheckOrderDTO> refundCheckOrderDTOMap;
                                        if (iRefundCheckOrderDTOS != null && !iRefundCheckOrderDTOS.isEmpty()) {
                                            refundCheckOrderDTOMap = iRefundCheckOrderDTOS.stream()
                                                    .collect(Collectors.toMap(IRefundCheckOrderDTO::getTransactionId, item ->
                                                            new RefundCheckOrderDTO(item.getTransactionId(), item.getRefundCount(),
                                                                    item.getAmountRefunded())));
                                        } else {
                                            refundCheckOrderDTOMap = new HashMap<>();
                                        }

                                        boolean allTransTypeD = responseDTOs.stream().allMatch(item -> "D".equals(item.getTransType()));
                                        if (allTransTypeD) {
                                            List<CheckOrderTransTypeDDTO> responseD = responseDTOs.stream().map(item -> {
                                                CheckOrderTransTypeDDTO checkOrderDTO = new CheckOrderTransTypeDDTO();
                                                checkOrderDTO.setAmount(item.getAmount());
                                                checkOrderDTO.setStatus(item.getStatus());
                                                checkOrderDTO.setNote(StringUtil.getValueNullChecker(item.getNote()));
                                                checkOrderDTO.setContent(item.getContent());
                                                checkOrderDTO.setOrderId(item.getOrderId());
                                                checkOrderDTO.setReferenceNumber(item.getReferenceNumber());
                                                checkOrderDTO.setTerminalCode(StringUtil.getValueNullChecker(item.getTerminalCode()));
                                                checkOrderDTO.setTimeCreated(item.getTimeCreated());
                                                checkOrderDTO.setTimePaid(item.getTimePaid());
                                                String checkExistRefundReferenceNumber =
                                                        transactionRefundService.checkExistRefundTransaction(dto.getBankAccount(),
                                                                item.getReferenceNumber());
                                                if (StringUtil.isNullOrEmpty(checkExistRefundReferenceNumber)) {
                                                    checkOrderDTO.setType(item.getType());
                                                } else {
                                                    checkOrderDTO.setType(6);
                                                }
                                                checkOrderDTO.setTransType(item.getTransType());
                                                return checkOrderDTO;
                                            }).collect(Collectors.toList());
                                            result = responseD;
                                        } else {
                                            response = responseDTOs.stream().map(item -> {
                                                TransReceiveResponseCheckOrderDTO checkOrderDTO = new TransReceiveResponseCheckOrderDTO();
                                                checkOrderDTO.setAmount(item.getAmount());
                                                checkOrderDTO.setStatus(item.getStatus());
                                                checkOrderDTO.setNote(StringUtil.getValueNullChecker(item.getNote()));
                                                checkOrderDTO.setContent(item.getContent());
                                                checkOrderDTO.setOrderId(item.getOrderId());
                                                checkOrderDTO.setReferenceNumber(item.getReferenceNumber());
                                                checkOrderDTO.setTerminalCode(StringUtil.getValueNullChecker(item.getTerminalCode()));
                                                checkOrderDTO.setTimeCreated(item.getTimeCreated());
                                                checkOrderDTO.setTimePaid(item.getTimePaid());
                                                checkOrderDTO.setType(item.getType());
                                                checkOrderDTO.setTransType(item.getTransType());
                                                RefundCheckOrderDTO refundCheckOrderDTO = refundCheckOrderDTOMap
                                                        .getOrDefault(item.getTransactionId(), new RefundCheckOrderDTO(item.getTransactionId()));
                                                checkOrderDTO.setRefundCount(refundCheckOrderDTO.getRefundCount());
                                                checkOrderDTO.setAmountRefunded(refundCheckOrderDTO.getAmountRefunded());
                                                return checkOrderDTO;
                                            }).collect(Collectors.toList());
                                            result = response;
                                        }
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        logger.error("checkTransactionStatus: NOT FOUND TRANSACTION");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E96");
                                    }
                                } else {
                                    logger.error("checkTransactionStatus: INVALID CHECK TYPE");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E95");
                                }
                            } else {
                                System.out.println("checkTransactionStatus: INVALID REQUEST BODY");
                                logger.error("checkTransactionStatus: INVALID REQUEST BODY");
                                result = new ResponseMessageDTO("FAILED", "E46");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            logger.error("checkTransactionStatus: INVALID CHECKSUM");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = new ResponseMessageDTO("FAILED", "E39");
                        }
                    } else {
                        System.out.println("checkTransactionStatus: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                        logger.error("checkTransactionStatus: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                        result = new ResponseMessageDTO("FAILED", "E77");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    System.out.println("checkTransactionStatus: INVALID REQUEST BODY");
                    logger.error("checkTransactionStatus: INVALID REQUEST BODY");
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                System.out.println("checkTransactionStatus: INVALID TOKEN");
                logger.error("checkTransactionStatus: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("checkTransactionStatus: ERROR: " + e.toString());
            logger.error("checkTransactionStatus: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getUsernameFromToken(String token) {
        String result = "";
        if (token != null && !token.trim().isEmpty()) {
            String secretKey = "mySecretKey";
            String jwtToken = token.substring(7); // remove "Bearer " from the beginning
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
            String userId = (String) claims.get("user");
            if (userId != null) {
                result = new String(Base64.getDecoder().decode(userId));
            }
        }
        return result;
    }

    ///
    // Get detail transaction QR LINK
    @GetMapping("transactions/qr-link")
    public ResponseEntity<Object> getTransactionQR(@RequestParam(value = "refId") String refId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (refId != null && !refId.trim().isEmpty()) {
                String id = TransactionRefIdUtil.decryptTransactionId(refId);
                TransactionQRDTO dto = transactionReceiveService.getTransactionQRById(id);
                if (dto != null) {
                    // generated QR
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    String caiValue = caiBankService.getCaiValue(dto.getBankTypeId());
                    vietQRGenerateDTO.setCaiValue(caiValue);
                    vietQRGenerateDTO.setBankAccount(dto.getBankAccount());
                    String amount = "";
                    String content = "";
                    if (dto.getAmount() != null) {
                        amount = dto.getAmount() + "";
                    } else {
                        amount = "0";
                    }
                    if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                        content = dto.getContent();
                    }
                    vietQRGenerateDTO.setAmount(amount);
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    // check bankAccount if mms_active = true
                    //
                    if (dto.getMmsActive() != null && dto.getMmsActive() == true) {
                        if (dto.getQrCode() != null && !dto.getQrCode().trim().isEmpty()) {
                            qr = dto.getQrCode();
                        }
                    }
                    // process response
                    TransactionQRResponseDTO responseDTO = new TransactionQRResponseDTO();
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setQr(qr);
                    responseDTO.setAmount(dto.getAmount());
                    responseDTO.setContent(dto.getContent());
                    responseDTO.setTransType(dto.getTransType());
                    responseDTO.setTerminalCode(dto.getTerminalCode());
                    responseDTO.setOrderId(dto.getOrderId());
                    responseDTO.setSign("");
                    responseDTO.setType(dto.getType());
                    responseDTO.setStatus(dto.getStatus());
                    responseDTO.setTimeCreated(dto.getTimeCreated());
                    responseDTO.setBankTypeId(dto.getBankTypeId());
                    responseDTO.setBankAccount(dto.getBankAccount());
                    responseDTO.setBankCode(dto.getBankCode());
                    responseDTO.setBankName(dto.getBankName());
                    responseDTO.setBankShortName(dto.getBankShortName());
                    responseDTO.setImgId(dto.getImgId());
                    responseDTO.setUserBankName(dto.getUserBankName());
                    responseDTO.setNote(dto.getNote());
                    // get merchant
                    String merchant = "";
                    String merchantQuery = accountCustomerBankService.getMerchantByBankId(dto.getBankId());
                    if (merchantQuery != null && !merchantQuery.trim().isEmpty()) {
                        merchant = merchantQuery;
                    }
                    responseDTO.setMerchant(merchant);
                    result = responseDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getTransactionQR: NOT FOUND TRANSACTION");
                    result = new ResponseMessageDTO("FAILED", "E94");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getTransactionQR: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getTransactionQR: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transactions/qr-link/cancel")
    public ResponseEntity<ResponseMessageDTO> cancelTransaction(
            @RequestBody TransQRCancelDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getRefId() != null && !dto.getRefId().trim().isEmpty()) {
                    String id = TransactionRefIdUtil.decryptTransactionId(dto.getRefId());
                    if (id != null && !id.trim().isEmpty()) {
                        // update status
                        transactionReceiveService.updateTransactionStatusById(2, id);
                        // push to QR Link Websocket
                        Map<String, String> data = new HashMap<>();
                        data.put("notificationType", NotificationUtil.getNotiTypeCancelTransaction());
                        try {
                            // send msg to QR Link
                            // String refId = TransactionRefIdUtil
                            // .encryptTransactionId(dto.getRefId());
                            socketHandler.sendMessageToTransactionRefId(dto.getRefId(), data);
                        } catch (IOException e) {
                            logger.error(
                                    "cancelTransaction: WS: socketHandler.sendMessageToUser ERROR: "
                                            + e.toString());
                        }
                        // return
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        logger.error("cancelTransaction: INVALID REF ID");
                        result = new ResponseMessageDTO("FAILED", "E97");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("cancelTransaction: INVALID REQUEST BODY");
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("cancelTransaction: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("cancelTransaction: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transactions/note")
    public ResponseEntity<ResponseMessageDTO> updateTransactionReceiveNote(
            @RequestBody TransactionReceiveNoteUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                transactionReceiveService.updateTransactionReceiveNote(dto.getNote(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateTransactionReceiveNote: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateTransactionReceiveNote: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @PostMapping("transactions/hashtag")
    public ResponseEntity<ResponseMessageDTO> updateTransactionReceiveHashTag(
            @Valid @RequestBody TransactionReceiveNoteUpdateDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateTransactionReceiveHashTag: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @GetMapping("transactions/merchant/statistic")
    public ResponseEntity<List<TransStatisticMerchantDTO>> getMerchantTransStatistic(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "time") String time) {
        List<TransStatisticMerchantDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // value:
            // 0: merchantId - year
            // 1: merchantId - month
            // 2: bankId - year
            // 3: bankId - month
            if (type == 0) {
                result = transactionReceiveService.getStatisticYearByMerchantId(id, time);
                if (result != null && !result.isEmpty()) {
                    TransStatisticMerchantDTOImpl sumDTO = createSumObject(result);
                    result.add(0, sumDTO);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 1) {
                result = transactionReceiveService.getStatisticMonthByMerchantId(id, time);
                if (result != null && !result.isEmpty()) {
                    TransStatisticMerchantDTOImpl sumDTO = createSumObject(result);
                    result.add(0, sumDTO);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 2) {
                result = transactionReceiveService.getStatisticYearByBankId(id, time);
                if (result != null && !result.isEmpty()) {
                    TransStatisticMerchantDTOImpl sumDTO = createSumObject(result);
                    result.add(0, sumDTO);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 3) {
                result = transactionReceiveService.getStatisticMonthByBankId(id, time);
                if (result != null && !result.isEmpty()) {
                    TransStatisticMerchantDTOImpl sumDTO = createSumObject(result);
                    result.add(0, sumDTO);
                }
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getMerchantTransStatistic: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private TransStatisticMerchantDTOImpl createSumObject(List<TransStatisticMerchantDTO> statisticList) {
        TransStatisticMerchantDTOImpl sumObject = statisticList.stream()
                .reduce(new TransStatisticMerchantDTOImpl("Tất cả", 0L, 0L, 0L, 0L, 0L, 0L),
                        (partialSum, dto) -> new TransStatisticMerchantDTOImpl(
                                "Tất cả",
                                partialSum.getTotalTrans() + dto.getTotalTrans(),
                                partialSum.getTotalAmount() + dto.getTotalAmount(),
                                partialSum.getTotalCredit() + dto.getTotalCredit(),
                                partialSum.getTotalDebit() + dto.getTotalDebit(),
                                partialSum.getTotalTransC() + dto.getTotalTransC(),
                                partialSum.getTotalTransD() + dto.getTotalTransD()),
                        (a, b) -> new TransStatisticMerchantDTOImpl(
                                "Total",
                                a.getTotalTrans() + b.getTotalTrans(),
                                a.getTotalAmount() + b.getTotalAmount(),
                                a.getTotalCredit() + b.getTotalCredit(),
                                a.getTotalDebit() + b.getTotalDebit(),
                                a.getTotalTransC() + b.getTotalTransC(),
                                a.getTotalTransD() + b.getTotalTransD()));

        return sumObject;
    }

    private String formatAmountNumber(String amount) {
        String result = amount;
        try {
            if (StringUtil.containsOnlyDigits(amount)) {
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                Long numberAmount = Long.parseLong(amount);
                result = nf.format(numberAmount);
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
