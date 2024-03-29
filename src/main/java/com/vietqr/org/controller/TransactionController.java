package com.vietqr.org.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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
    AccountCustomerBankService accountCustomerBankService;

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

    @GetMapping("user/transactions")
    public ResponseEntity<List<TransactionReceiveAdminListDTO>> getTransactionUser(
            @RequestParam(value = "userId") String userId,
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
//            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
//            if (checkEmptyDate) {
//                switch (type) {
//                    case 0:
//                        result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 1:
//                        result = transactionReceiveService.getTransByFtCodeAndUserId(value, userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 2:
//                        result = transactionReceiveService.getTransByOrderIdAndUserId(value, userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 3:
//                        value = value.replace("-", " ").trim();
//                        result = transactionReceiveService.getTransByContentAndUserId(value, userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 4:
//                        result = transactionReceiveService.getTransByTerminalCodeAndUserIdAllDate(value, userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 9:
//                        result = transactionReceiveService.getAllTransAllDateByUserId(userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    default:
//                        logger.error("getTransactionUser: ERROR: INVALID TYPE");
//                        httpStatus = HttpStatus.BAD_REQUEST;
//                        break;
//                }
//            } else {
//                switch (type) {
//                    case 0:
//                        String check = accountBankReceiveShareService.checkUserExistedFromBankAccount(userId, value);
//                        if (StringUtil.isNullOrEmpty(check)) {
//                            result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
//                        } else {
//                            result = transactionReceiveService.getTransByBankAccountFromDateTerminal(userId, value, fromDate, toDate, offset);
//                        }
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 1:
//                        result = transactionReceiveService.getTransByFtCodeAndUserId(value, userId, offset, fromDate, toDate);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 2:
//                        result = transactionReceiveService.getTransByOrderIdAndUserId(value, userId, offset, fromDate, toDate);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 3:
//                        value = value.replace("-", " ").trim();
//                        result = transactionReceiveService.getTransByContentAndUserId(value, userId, offset, fromDate, toDate);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 4:
//                        ////
//                        String id = accountBankReceiveShareService.checkUserExistedFromBankByTerminalCode(value, userId);
//                        if (id != null && !id.isEmpty()) {
//                            result = transactionReceiveService.getTransByTerminalCodeFromDateTerminal(fromDate, toDate, value, userId, offset);
//                        } else {
//                            result = transactionReceiveService.getTransByTerminalCodeAndUserIdFromDate(fromDate, toDate, value,
//                                    userId, offset);
//                        }
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    case 9:
//                        result = transactionReceiveService.getAllTransFromDateByUserId(fromDate, toDate, userId, offset);
//                        httpStatus = HttpStatus.OK;
//                        break;
//                    default:
//                        logger.error("getTransactionUser : ERROR: INVALID TYPE");
//                        httpStatus = HttpStatus.BAD_REQUEST;
//                        break;
//                }
//            }
            result = new ArrayList<>();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
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
            List<String> listCode = new ArrayList<>();
            boolean checkEmptyDate = StringUtil.isEmptyOrEqualsZero(fromDate) || StringUtil.isEmptyOrEqualsZero(toDate);
            boolean checkEmptyTerminal = StringUtil.isNullOrEmpty(terminalCode);
            List<String> terminalCodeAccess = accountBankReceiveShareService.checkUserExistedFromBankId(userId, bankId);
            if (terminalCodeAccess != null && !terminalCodeAccess.isEmpty()) {
                if (!checkEmptyTerminal) {
                    listCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(terminalCode);
                    listCode.add(terminalCode);
                } else {
                    listCode = terminalBankReceiveService.getTerminalCodeByMainTerminalCodeList(terminalCodeAccess);
                    listCode.addAll(terminalCodeAccess);
                }
//                if (checkEmptyTerminal && checkEmptyDate) {
//                    switch (type) {
//                        case 1:
//                            dtos = transactionReceiveService.getTransTerminalByFtCode(bankId, userId, value, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 2:
//                            dtos = transactionReceiveService.getTransTerminalByOrderId(bankId, userId, value, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 3:
//                            value = value.replace("-", " ").trim();
//                            dtos = transactionReceiveService.getTransTerminalByContent(bankId, userId, value, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 5:
//                            dtos = transactionReceiveService.getTransTerminalByStatus(bankId, userId, Integer.parseInt(value), offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 9:
//                            dtos = transactionReceiveService.getAllTransTerminal(bankId, userId, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        default:
//                            logger.error("getTransactionUser: ERROR: INVALID TYPE");
//                            httpStatus = HttpStatus.BAD_REQUEST;
//                            break;
//                    }
//                } else if (checkEmptyTerminal && !checkEmptyDate) {
//                    switch (type) {
//                        case 1:
//                            dtos = transactionReceiveService
//                                    .getTransTerminalByFtCode(bankId, userId, value, fromDate, toDate, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 2:
//                            dtos = transactionReceiveService
//                                    .getTransTerminalByOrderId(bankId, userId, value, fromDate, toDate, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 3:
//                            value = value.replace("-", " ").trim();
//                            dtos = transactionReceiveService
//                                    .getTransTerminalByContent(bankId, userId, value, fromDate, toDate, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 5:
//                            dtos = transactionReceiveService
//                                    .getTransTerminalByStatus(bankId, userId, Integer.parseInt(value), fromDate, toDate, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        case 9:
//                            dtos = transactionReceiveService
//                                    .getAllTransTerminal(bankId, userId, fromDate, toDate, offset);
//                            httpStatus = HttpStatus.OK;
//                            break;
//                        default:
//                            logger.error("getTransactionUser: ERROR: INVALID TYPE");
//                            httpStatus = HttpStatus.BAD_REQUEST;
//                            break;
//                    }
//                } else
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
                            dtos = transactionReceiveService
                                    .getTransTerminalByFtCode(bankId, userId, value, listCode, fromDate, toDate, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 2:
                            dtos = transactionReceiveService
                                    .getTransTerminalByOrderId(bankId, userId, value, listCode, fromDate, toDate, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 3:
                            value = value.replace("-", " ").trim();
                            dtos = transactionReceiveService
                                    .getTransTerminalByContent(bankId, userId, value, listCode, fromDate, toDate, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 5:
                            dtos = transactionReceiveService
                                    .getTransTerminalByStatus(bankId, userId, Integer.parseInt(value), listCode, fromDate, toDate, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        case 9:
                            dtos = transactionReceiveService
                                    .getAllTransTerminal(bankId, userId, listCode, fromDate, toDate, offset);
                            httpStatus = HttpStatus.OK;
                            break;
                        default:
                            logger.error("getTransactionUser: ERROR: INVALID TYPE");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            break;
                    }
                }
                String bankShortName = accountBankReceiveService.getBankShortNameByBankId(bankId);
                result = dtos.stream().map(dto -> {
                    TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                    responseDTO.setTransactionId(dto.getTransactionId());
                    responseDTO.setReferenceNumber(dto.getReferenceNumber() != null ? dto.getReferenceNumber() : "");
                    responseDTO.setBankAccount(dto.getBankAccount());
                    responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                    responseDTO.setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "");
                    responseDTO.setTransType(dto.getTransType());
                    responseDTO.setAmount(dto.getAmount());
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
                logger.error("getTransactionUser: ERROR: INVALID USER");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    ////// export transaction
    // @GetMapping("merchant/transactions-export")
    // public ResponseEntity<byte[]> exporTransactionMerchant(
    // @RequestParam(value = "merchantId") String merchantId,
    // @RequestParam(value = "type") int type,
    // @RequestParam(value = "value") String value,
    // @RequestParam(value = "from") String fromDate,
    // @RequestParam(value = "to") String toDate,
    // HttpServletResponse response) {
    // List<TransactionReceiveAdminListDTO> list = new ArrayList<>();

    // try {
    // // type (search by)
    // // - 0: bankAccount
    // // - 1: reference_number (FT Code)
    // // - 2: order_id
    // // - 3: content
    // // - 4: terminal code
    // // - 9: all
    // String sheetName = "VietQRVN-Transaction";
    // if (type == 0) {
    // if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") &&
    // !toDate.trim().isEmpty()
    // && !toDate.trim().equals("0")) {
    // list = transactionReceiveService.exportTransByBankAccountFromDate(value,
    // fromDate, toDate);
    // }
    // } else if (type == 1) {
    // list = transactionReceiveService.exportTransByFtCodeAndMerchantId(value,
    // merchantId);
    // } else if (type == 2) {
    // list = transactionReceiveService.exportTransByOrderIdAndMerchantId(value,
    // merchantId);
    // } else if (type == 3) {
    // value = value.replace("-", " ").trim();
    // list = transactionReceiveService.exportTransByContentAndMerchantId(value,
    // merchantId);
    // } else if (type == 4) {
    // if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") &&
    // !toDate.trim().isEmpty()
    // && !toDate.trim().equals("0")) {
    // list =
    // transactionReceiveService.exportTransFromDateByTerminalCodeAndMerchantId(fromDate,
    // toDate,
    // value, merchantId);
    // }
    // } else if (type == 9) {
    // if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") &&
    // !toDate.trim().isEmpty()
    // && !toDate.trim().equals("0")) {
    // list = transactionReceiveService.exportAllTransFromDateByMerchantId(fromDate,
    // toDate, merchantId);
    // // System.out.println("TRUE DATE - list size: " + list.size());
    // }
    // // else {
    // // // System.out.println("WRONG DATE");
    // // }
    // } else {
    // logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
    // }
    // sheetName = sheetName.replace(":", "");
    // if (list != null && !list.isEmpty()) {
    // // Create a new workbook and sheet
    // Workbook workbook = new XSSFWorkbook();
    // Sheet sheet = workbook.createSheet(sheetName);
    // // Create a header row
    // Row headerRow = sheet.createRow(0);
    // headerRow.createCell(0).setCellValue("STT");
    // headerRow.createCell(1).setCellValue("Số TK");
    // headerRow.createCell(2).setCellValue("Ngân hàng");
    // headerRow.createCell(3).setCellValue("Mã đơn hàng");
    // headerRow.createCell(4).setCellValue("Mã mã GD");
    // headerRow.createCell(5).setCellValue("Thu (VND)");
    // headerRow.createCell(6).setCellValue("Chi (VND)");
    // headerRow.createCell(7).setCellValue("Trạng thái");
    // headerRow.createCell(8).setCellValue("Thời gian tạo GD");
    // headerRow.createCell(9).setCellValue("Thời gian TT");
    // headerRow.createCell(10).setCellValue("Mã điểm bán");
    // headerRow.createCell(11).setCellValue("Nội dung");
    // headerRow.createCell(12).setCellValue("Loại GD");
    // headerRow.createCell(13).setCellValue("Ghi chú");
    // int counter = 0;
    // int rowNum = 1;
    // for (TransactionReceiveAdminListDTO item : list) {
    // counter++;
    // Row row = sheet.createRow(rowNum++);
    // row.createCell(0).setCellValue(counter + "");
    // row.createCell(1).setCellValue(item.getBankAccount());
    // row.createCell(2).setCellValue(item.getBankShortName());

    // String orderId = "";
    // if (item.getOrderId() != null && !item.getOrderId().trim().isEmpty()) {
    // orderId = item.getOrderId();
    // }
    // row.createCell(3).setCellValue(orderId);
    // String refNumber = "";
    // if (item.getReferenceNumber() != null &&
    // !item.getReferenceNumber().trim().isEmpty()) {
    // refNumber = item.getReferenceNumber();
    // }
    // row.createCell(4).setCellValue(refNumber);
    // if (item.getTransType().toUpperCase().equals("C")) {
    // row.createCell(5).setCellValue(item.getAmount());
    // row.createCell(6).setCellValue("");
    // } else {
    // row.createCell(5).setCellValue("");
    // row.createCell(6).setCellValue(item.getAmount());
    // }
    // String status = "";
    // if (item.getStatus() == 0) {
    // status = "Chờ thanh toán";
    // } else if (item.getStatus() == 1) {
    // status = "Thành công";
    // } else if (item.getStatus() == 2) {
    // status = "Đã huỷ";
    // }
    // row.createCell(7).setCellValue(status);
    // row.createCell(8).setCellValue(generateTime(item.getTimeCreated()));
    // row.createCell(9).setCellValue(generateTime(item.getTimePaid()));
    // String terminalCode = "-";
    // if (item.getTerminalCode() != null &&
    // !item.getTerminalCode().trim().isEmpty()) {
    // terminalCode = item.getTerminalCode();
    // }
    // row.createCell(10).setCellValue(terminalCode);
    // row.createCell(11).setCellValue(item.getContent());
    // String typeTrans = "-";
    // if (item.getType() == 0) {
    // typeTrans = "Mã VietQR";
    // } else if (item.getType() == 2) {
    // typeTrans = "Khác";
    // }
    // row.createCell(12).setCellValue(typeTrans);
    // String note = "-";
    // if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
    // note = item.getNote();
    // }
    // row.createCell(13).setCellValue(note);
    // }
    // //
    // for (int i = 0; i < 9; i++) {
    // sheet.autoSizeColumn(i);
    // }
    // // Set the response headers
    // response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    // response.setHeader("Content-Disposition", "attachment; filename=" + sheetName
    // + ".xlsx");

    // // Write the workbook data directly to the response output stream
    // workbook.write(response.getOutputStream());
    // workbook.close();

    // }
    // } catch (Exception e) {
    // logger.error("getTransactionAdmin: ERROR: " + e.toString());
    // System.out.println("ERROR EXPORT: " + e.toString());
    // }
    // return new ResponseEntity<>(HttpStatus.OK);
    // }

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
//            String isOwner = accountBankReceiveShareService.checkUserExistedFromBankAccountAndIsOwner(userId, bankId);
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
                    result = dtos.stream().map(item -> {
                        TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                        trans.setId(item.getId());
                        trans.setBankAccount(item.getBankAccount());
                        trans.setAmount(item.getAmount());
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
                result = new ArrayList<>();
                httpStatus = HttpStatus.BAD_REQUEST;
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
            result = new PageResultDTO(page, size, (int) Math.ceil(totalPage), totalElement, dtos);

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
                    .getTransactionReceiveById(dto.getTransactionId(), dto.getUserId());
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
            result = dtos.stream().map(dto -> {
                TransactionRelatedResponseDTO responseDTO = new TransactionRelatedResponseDTO();
                responseDTO.setTransactionId(dto.getTransactionId());
                responseDTO.setReferenceNumber(dto.getReferenceNumber());
                responseDTO.setBankAccount(dto.getBankAccount());
                responseDTO.setBankShortName(bankShortName != null ? bankShortName : "");
                responseDTO.setOrderId(dto.getOrderId());
                responseDTO.setTransType(dto.getTransType());
                responseDTO.setAmount(dto.getAmount());
                responseDTO.setStatus(dto.getStatus());
                responseDTO.setTime(dto.getTime());
                responseDTO.setTimePaid(dto.getTimePaid());
                responseDTO.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                responseDTO.setContent(dto.getContent());
                responseDTO.setType(dto.getType());
                responseDTO.setNote(dto.getNote() != null ? dto.getNote() : "");
                return responseDTO;

            }).collect(Collectors.toList());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction/{id}")
    public ResponseEntity<TransactionDetailDTO> getTransactionById(@PathVariable(value = "id") String id) {
        TransactionDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.getTransactionById(id);
            System.out.println(id);
            System.out.println(result.toString());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            System.out.println(e.toString());
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
                result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;

            } else if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverviewNotSync(bankId, terminalCode, month);
                if (dto != null) {
                    result = new TransStatisticResponseDTO();
                    result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                    result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                    result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                    result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                    result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                    httpStatus = HttpStatus.OK;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (dto != null) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;
            }
            else {
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
                result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;

            } else if (!StringUtil.isNullOrEmpty(checkIsOwner) && !StringUtil.isNullOrEmpty(terminalCode)) {
                dto = transactionReceiveService.getTransactionOverviewNotSync(bankId, terminalCode, fromDate, toDate);
                if (dto != null) {
                    result = new TransStatisticResponseDTO();
                    result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                    result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                    result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                    result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                    result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                    httpStatus = HttpStatus.OK;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (dto != null) {
                result = new TransStatisticResponseDTO();
                result.setTotalCashIn(dto.getTotalCashIn()!= null ? dto.getTotalCashIn() : 0);
                result.setTotalCashOut(dto.getTotalCashOut()!= null ? dto.getTotalCashOut() : 0);
                result.setTotalTransC(dto.getTotalTransC()!= null ? dto.getTotalTransC() : 0);
                result.setTotalTransD(dto.getTotalTransD()!= null ? dto.getTotalTransD() : 0);
                result.setTotalTrans(dto.getTotalTrans()!= null ? dto.getTotalTrans() : 0);
                httpStatus = HttpStatus.OK;
            }
            else {
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
                }  else {
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
                            List<TransReceiveResponseDTO> response = new ArrayList<>();
                            if (dto.getValue() != null && !dto.getValue().trim().isEmpty()) {
                                if (dto.getType() != null && dto.getType() == 0) {
                                    response = transactionReceiveService.getTransByOrderId(dto.getValue(),
                                            dto.getBankAccount());
                                    if (response != null && !response.isEmpty()) {
                                        result = response;
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        logger.error("checkTransactionStatus: NOT FOUND TRANSACTION");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E96");
                                    }
                                } else if (dto.getType() != null && dto.getType() == 1) {
                                    response = transactionReceiveService.getTransByReferenceNumber(dto.getValue(),
                                            dto.getBankAccount());
                                    if (response != null && !response.isEmpty()) {
                                        result = response;
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
}
