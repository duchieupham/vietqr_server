package com.vietqr.org.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.vietqr.org.dto.TransImgIdDTO;
import com.vietqr.org.dto.TransactionBranchInputDTO;
import com.vietqr.org.dto.TransactionCheckDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDateDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionInputDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.TransactionReceiveImageEntity;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.TransactionBankService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveImageService;
import com.vietqr.org.service.TransactionReceiveService;

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
}
