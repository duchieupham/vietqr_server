package com.vietqr.org.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransByCusSyncDTO;
import com.vietqr.org.dto.TransImgIdDTO;
import com.vietqr.org.dto.TransReceiveAdminDetailDTO;
import com.vietqr.org.dto.TransReceiveRpaDTO;
import com.vietqr.org.dto.TransStatisticByDateDTO;
import com.vietqr.org.dto.TransStatisticByMonthDTO;
import com.vietqr.org.dto.TransStatisticDTO;
import com.vietqr.org.dto.TransSyncRpaDTO;
import com.vietqr.org.dto.TransactionBranchInputDTO;
import com.vietqr.org.dto.TransactionCheckDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDateDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionInputDTO;
import com.vietqr.org.dto.TransactionReceiveAdminListDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.TransactionRPAEntity;
import com.vietqr.org.entity.TransactionReceiveImageEntity;
import com.vietqr.org.entity.TransactionReceiveLogEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.TransactionBankService;
import com.vietqr.org.service.TransactionRPAService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveImageService;
import com.vietqr.org.service.TransactionReceiveLogService;
import com.vietqr.org.service.TransactionReceiveService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionBankService transactionBankService;

    @Autowired
    ImageService imageService;

    @Autowired
    TransactionReceiveImageService transactionReceiveImageService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    TransactionRPAService transactionRPAService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @PostMapping("transaction-branch")
    public ResponseEntity<List<TransactionRelatedDTO>> getTransactionsByBranchId(
            @Valid @RequestBody TransactionBranchInputDTO dto) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (dto.getBranchId().trim().equals("all")) {
                // get transaction by businessId
                result = transactionReceiveBranchService.getTransactionsByBusinessId(dto.getBusinessId(),
                        dto.getOffset());
            } else {
                // get transaction by branchId
                result = transactionReceiveBranchService.getTransactionsByBranchId(dto.getBranchId(), dto.getOffset());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

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
            // - 9: all
            if (type == 0) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
                } else {
                    result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 1) {
                result = transactionReceiveService.getTransByFtCode(value, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 2) {
                result = transactionReceiveService.getTransByOrderId(value, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 3) {
                value = value.replace("-", " ").trim();
                result = transactionReceiveService.getTransByContent(value, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 9) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getAllTransFromDate(fromDate, toDate, offset);
                } else {
                    result = transactionReceiveService.getAllTransAllDate(offset);
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
                httpStatus = HttpStatus.BAD_REQUEST;
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
            if (type == 0) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
                } else {
                    result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 1) {
                result = transactionReceiveService.getTransByFtCodeAndMerchantId(value, merchantId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 2) {
                result = transactionReceiveService.getTransByOrderIdAndMerchantId(value, merchantId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 3) {
                value = value.replace("-", " ").trim();
                result = transactionReceiveService.getTransByContentAndMerchantId(value, merchantId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 9) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getAllTransFromDateByMerchantId(fromDate, toDate, merchantId,
                            offset);
                } else {
                    result = transactionReceiveService.getAllTransAllDateByMerchantId(merchantId, offset);
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
                httpStatus = HttpStatus.BAD_REQUEST;
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
            if (type == 0) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
                } else {
                    result = transactionReceiveService.getTransByBankAccountAllDate(value, offset);
                }
                httpStatus = HttpStatus.OK;
            } else if (type == 1) {
                result = transactionReceiveService.getTransByFtCodeAndUserId(value, userId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 2) {
                result = transactionReceiveService.getTransByOrderIdAndUserId(value, userId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 3) {
                value = value.replace("-", " ").trim();
                result = transactionReceiveService.getTransByContentAndUserId(value, userId, offset);
                httpStatus = HttpStatus.OK;
            } else if (type == 9) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    result = transactionReceiveService.getAllTransFromDateByUserId(fromDate, toDate, userId,
                            offset);
                } else {
                    result = transactionReceiveService.getAllTransAllDateByUserId(userId, offset);
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getTransactionAdmin: ERROR: INVALID TYPE");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    ////// export transaction
    @GetMapping("merchant/transactions-export")
    public ResponseEntity<byte[]> exporTransactionMerchant(
            @RequestParam(value = "merchantId") String merchantId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            HttpServletResponse response) {
        List<TransactionReceiveAdminListDTO> list = new ArrayList<>();

        try {
            // type (search by)
            // - 0: bankAccount
            // - 1: reference_number (FT Code)
            // - 2: order_id
            // - 3: content
            // - 9: all
            String sheetName = "VietQRVN-Transaction";
            if (type == 0) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    list = transactionReceiveService.exportTransByBankAccountFromDate(value, fromDate, toDate);
                    sheetName = value + "-from" + fromDate + "-to" + toDate;
                }

            } else if (type == 1) {
                list = transactionReceiveService.exportTransByFtCodeAndMerchantId(value, merchantId);
                sheetName = "VietQRVN-Transaction-" + value;

            } else if (type == 2) {
                list = transactionReceiveService.exportTransByOrderIdAndMerchantId(value, merchantId);
                sheetName = "VietQRVN-Transaction-" + value;

            } else if (type == 3) {
                value = value.replace("-", " ").trim();
                list = transactionReceiveService.exportTransByContentAndMerchantId(value, merchantId);
                sheetName = "VietQRVN-Transaction-" + value;

            } else if (type == 9) {
                if (!fromDate.trim().isEmpty() && !fromDate.trim().equals("0") && !toDate.trim().isEmpty()
                        && !toDate.trim().equals("0")) {
                    list = transactionReceiveService.exportAllTransFromDateByMerchantId(fromDate, toDate, merchantId);
                    sheetName = "VietQRVN-Transaction-" + fromDate + "-" + toDate;
                }

            } else {
                logger.error("getTransactionAdmin: ERROR: INVALID TYPE");

            }
            if (list != null && !list.isEmpty()) {
                // Create a new workbook and sheet
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(sheetName);
                // Create a header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("STT");
                headerRow.createCell(1).setCellValue("Số TK");
                headerRow.createCell(2).setCellValue("Mã đơn hàng");
                headerRow.createCell(3).setCellValue("Mã mã GD");
                headerRow.createCell(4).setCellValue("Số tiền");
                headerRow.createCell(5).setCellValue("Trạng thái");
                headerRow.createCell(6).setCellValue("Thời gian tạo GD");
                headerRow.createCell(7).setCellValue("Thời gian TT");
                headerRow.createCell(8).setCellValue("Nội dung");
                headerRow.createCell(9).setCellValue("Loại GD");

                int counter = 0;
                int rowNum = 1;
                for (TransactionReceiveAdminListDTO item : list) {
                    counter++;
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(counter + "");
                    String bankAccount = item.getBankAccount() + "\n" + item.getBankShortName();
                    row.createCell(1).setCellValue(bankAccount);
                    String orderId = "-";
                    if (item.getOrderId() != null && !item.getOrderId().trim().isEmpty()) {
                        orderId = item.getOrderId();
                    }
                    row.createCell(2).setCellValue(orderId);
                    String refNumber = "-";
                    if (item.getReferenceNumber() != null && !item.getReferenceNumber().trim().isEmpty()) {
                        refNumber = item.getReferenceNumber();
                    }
                    row.createCell(3).setCellValue(refNumber);
                    String prefix = "";
                    if (item.getTransType().toUpperCase().equals("C")) {
                        prefix = "+";
                    } else {
                        prefix = "-";
                    }
                    row.createCell(4).setCellValue(prefix + formatCurrency(item.getAmount()) + " VND");
                    String status = "-";
                    if (item.getStatus() == 0) {
                        status = "Chờ thanh toán";
                    } else if (item.getStatus() == 1) {
                        status = "Thành công";
                    } else if (item.getStatus() == 2) {
                        status = "Đã huỷ";
                    }
                    row.createCell(5).setCellValue(status);
                    row.createCell(6).setCellValue(generateTime(item.getTimeCreated()));
                    row.createCell(7).setCellValue(generateTime(item.getTimePaid()));
                    row.createCell(8).setCellValue(item.getContent());
                    String typeTrans = "-";
                    if (item.getType() == 0) {
                        typeTrans = "Mã VietQR";
                    } else if (item.getType() == 2) {
                        typeTrans = "Khác";
                    }
                    row.createCell(9).setCellValue(typeTrans);
                }
                //
                for (int i = 0; i < 9; i++) {
                    sheet.autoSizeColumn(i);
                }
                // Set the response headers
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=" + sheetName + ".xlsx");

                // Write the workbook data directly to the response output stream
                workbook.write(response.getOutputStream());
                workbook.close();

            }
        } catch (Exception e) {
            logger.error("getTransactionAdmin: ERROR: " + e.toString());

        }
        return new ResponseEntity<>(HttpStatus.OK);
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

    String generateTime(long input) {
        String result = "";
        try {
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
            result = transactionReceiveService.getTransactions(dto.getOffset(), dto.getBankId());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transactions")
    public ResponseEntity<List<TransactionRelatedDTO>> getTransactionsFilter(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (status == 9) {
                result = transactionReceiveService.getTransactions(offset, bankId);
            } else {
                result = transactionReceiveService.getTransactionsByStatus(status, offset, bankId);
            }
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

    @GetMapping("transaction/overview/{bankId}")
    public ResponseEntity<TransStatisticDTO> getTransactionOverview(@PathVariable("bankId") String bankId) {
        TransStatisticDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.getTransactionOverview(bankId);
            if (result != null) {
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

    @GetMapping("transaction/statistic")
    public ResponseEntity<Object> getTransactionStatistic(
            @RequestParam(value = "bankId") String bankId,
            @RequestParam(value = "type") int type) {
        Object result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type = 1 => sort by date
            // type = 2 => sort by month
            if (type == 1) {
                List<TransStatisticByDateDTO> transactions = transactionReceiveService.getTransStatisticByDate(bankId);
                result = transactions;
                httpStatus = HttpStatus.OK;
            } else if (type == 2) {
                List<TransStatisticByMonthDTO> transactions = transactionReceiveService
                        .getTransStatisticByMonth(bankId);
                result = transactions;
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("Error at getTransactionStatistic: INVALID TYPE");
                httpStatus = HttpStatus.BAD_REQUEST;
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
            @RequestParam(value = "offset") int offset) {
        List<TransByCusSyncDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.getTransactionsByCustomerSync(bankId, customerSyncId, offset);
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
}
