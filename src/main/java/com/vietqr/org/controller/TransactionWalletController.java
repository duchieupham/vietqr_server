package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.VietQRUtil;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionWalletEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionWalletController {
    private static final Logger logger = Logger.getLogger(TransactionWalletController.class);

    @Autowired
    TransactionWalletService transactionWalletService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @PostMapping("transaction-wallet/request-payment")
    public ResponseEntity<ResponseMessageDTO> requestPayment(@RequestBody RequestPaymentDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String checkExisted = accountLoginService.checkExistedUserByIdAndPassword(dto.getUserId(),
                        dto.getPassword());
                if (checkExisted != null && !checkExisted.trim().isEmpty()) {
                    // 1. check payment type
                    // 2.1 if = 0. now ignore
                    // 2.2 if = 1. do
                    // 2.2.1 do insert transaction wallet => type = 0 & otp & payment type = 1
                    // 2.2.2 response otp
                    // 2.3 if = 2. now ignore
                    // 2.4 if != 1|2|3. response error
                    if (dto.getPaymentType() == 0) {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (dto.getPaymentType() == 1) {
                        // insert transaction_wallet
                        UUID transWalletUUID = UUID.randomUUID();
                        String billNumber = "VAF" + RandomCodeUtil.generateRandomId(10);
                        String otp = RandomCodeUtil.generateOTP(6);
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                        transactionWalletEntity.setId(transWalletUUID.toString());
                        transactionWalletEntity.setAmount("0");
                        transactionWalletEntity.setBillNumber(billNumber);
                        transactionWalletEntity.setContent("");
                        transactionWalletEntity.setStatus(0);
                        transactionWalletEntity.setTimeCreated(time);
                        transactionWalletEntity.setTimePaid(0);
                        transactionWalletEntity.setTransType("D");
                        transactionWalletEntity.setUserId(dto.getUserId());
                        transactionWalletEntity.setOtp(otp);
                        transactionWalletEntity.setPaymentType(dto.getPaymentType());
                        transactionWalletEntity.setPaymentMethod(0);
                        transactionWalletEntity.setReferenceNumber("");
                        transactionWalletEntity.setPhoneNoRC("");
                        transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                        //
                        result = new ResponseMessageDTO("SUCCESS", otp);
                        httpStatus = HttpStatus.OK;
                    } else if (dto.getPaymentType() == 2) {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        logger.error("requestPayment: WRONG PAYMENT TYPE");
                        result = new ResponseMessageDTO("FAILED", "E56");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("requestPayment: USER NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E55");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("requestPayment: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("requestPayment: Error " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction-wallet")
    public ResponseEntity<Object> insertTransactionWallet(@RequestBody TransWalletInsertDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String userId = accountLoginService.getUserIdByPhoneNo(dto.getPhoneNo());
                if (userId != null) {
                    UUID transReceiveUUID = UUID.randomUUID();
                    UUID transcationReceiveBranchUUID = UUID.randomUUID();
                    UUID transWalletUUID = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    //
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumber = "VRC" + RandomCodeUtil.generateRandomId(10);
                    String content = traceId + " " + billNumber;
                    // get bank recharge information
                    String bankAccount = EnvironmentUtil.getBankAccountRecharge();
                    String bankId = EnvironmentUtil.getBankIdRecharge();
                    String bankLogo = EnvironmentUtil.getBankLogoIdRecharge();
                    String cai = EnvironmentUtil.getCAIRecharge();
                    String businessId = EnvironmentUtil.getBusinessIdRecharge();
                    String branchId = EnvironmentUtil.getBranchIdRecharge();
                    String userIdHost = EnvironmentUtil.getUserIdHostRecharge();
                    // create VietQR

                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(cai);
                    vietQRGenerateDTO.setBankAccount(bankAccount);
                    vietQRGenerateDTO.setAmount(dto.getAmount());
                    vietQRGenerateDTO.setContent(content);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    // generate VietQRDTO
                    VietQRDTO vietQRDTO = new VietQRDTO();
                    vietQRDTO.setBankCode("");
                    vietQRDTO.setBankName("");
                    vietQRDTO.setBankAccount("");
                    vietQRDTO.setUserBankName("");
                    vietQRDTO.setAmount(dto.getAmount());
                    vietQRDTO.setContent(content);
                    vietQRDTO.setQrCode(qr);
                    vietQRDTO.setImgId(bankLogo);
                    // return transactionId to upload bill image
                    vietQRDTO.setTransactionId("");

                    // insert transaction_receive
                    TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
                    transactionReceiveEntity.setId(transReceiveUUID.toString());
                    transactionReceiveEntity.setAmount(Long.parseLong(dto.getAmount()));
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
                    transactionReceiveEntity.setOrderId(billNumber);
                    transactionReceiveEntity.setSign("");
                    transactionReceiveEntity.setCustomerBankAccount("");
                    transactionReceiveEntity.setCustomerBankCode("");
                    transactionReceiveEntity.setCustomerName("");
                    transactionReceiveEntity.setTerminalCode("");
                    transactionReceiveEntity.setUserId(userIdHost);
                    transactionReceiveEntity.setNote("");
                    transactionReceiveEntity.setTransStatus(0);
                    transactionReceiveEntity.setUrlLink("");
                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                    // insert transaction branch
//                    if (businessId != null && branchId != null && !businessId.trim().isEmpty()
//                            && !branchId.trim().isEmpty()) {
//                        TransactionReceiveBranchEntity transactionReceiveBranchEntity = new TransactionReceiveBranchEntity();
//                        transactionReceiveBranchEntity.setId(transcationReceiveBranchUUID.toString());
//                        transactionReceiveBranchEntity.setBusinessId(businessId);
//                        transactionReceiveBranchEntity.setBranchId(branchId);
//                        transactionReceiveBranchEntity.setTransactionReceiveId(transReceiveUUID.toString());
//                        transactionReceiveBranchService.insertTransactionReceiveBranch(transactionReceiveBranchEntity);
//                    }
                    // insert transaction_wallet
                    TransactionWalletEntity transactionWalletEntity = new TransactionWalletEntity();
                    transactionWalletEntity.setId(transWalletUUID.toString());
                    transactionWalletEntity.setAmount(dto.getAmount());
                    transactionWalletEntity.setBillNumber(billNumber);
                    transactionWalletEntity.setContent(content);
                    transactionWalletEntity.setStatus(0);
                    transactionWalletEntity.setTimeCreated(time);
                    transactionWalletEntity.setTimePaid(0);
                    transactionWalletEntity.setTransType("C");
                    transactionWalletEntity.setUserId(userId);
                    transactionWalletEntity.setOtp("");
                    transactionWalletEntity.setPaymentType(0);
                    transactionWalletEntity.setPaymentMethod(1);
                    transactionWalletEntity.setReferenceNumber("");
                    transactionWalletEntity.setPhoneNoRC("");
                    transactionWalletService.insertTransactionWallet(transactionWalletEntity);
                    // final
                    result = vietQRDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("insertTransactionWallet: USER NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E59");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertTransactionWallet: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertTransactionWallet: Error " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;

        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update giao dich thanh cong => transaction sync xu ly

    // get list all

    @GetMapping("transaction-wallet")
    public ResponseEntity<List<TransWalletListDTO>> getTransactionWalletsFilter(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset) {
        List<TransWalletListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (status == 9) {
                result = transactionWalletService.getTransactionWalletList(userId, offset);
            } else {
                result = transactionWalletService.getTransactionWalletListByStatus(userId, status, offset);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionWalletsFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list by userId

    // get chi tiết

    // get VNPT Epay Transaction
    @GetMapping("transaction-wallet/vnpt-epay")
    public ResponseEntity<List<TransactionVNPTItemDTO>> getTransactionsVNPTFilter(
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset) {
        List<TransactionVNPTItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (status == 9) {
                result = transactionWalletService.getTransactionsVNPT(offset);
            } else {
                result = transactionWalletService.getTransactionsVNPTFilter(status, offset);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionsVNPTFilter: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get VNPT Epay Statistic
    @GetMapping("transaction-wallet/vnpt-epay/statistic")
    public ResponseEntity<VNPTEpayTransCounterDTO> getVNPTEpayStatisticTransaction() {
        VNPTEpayTransCounterDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionWalletService.getVNPTEpayCounter();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getVNPTEpayStatisticTransaction: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("admin/transaction-wallet")
    public ResponseEntity<PageResponseDTO> getTransactionsWalletAdmin(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate,
            @RequestParam(value = "filterBy") int filterBy,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value) {
        DataDTO data = new DataDTO(new TransAdminWalletExtraData());
        PageResponseDTO result = new PageResponseDTO();
        result.setData(data);
//        List<TransactionWalletAdminDTO> response = new ArrayList<>();
//        List<TransactionWalletVNPTEpayDTO> vnptEpays = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            int offset = (page - 1) * size;
            int totalElement = 0;
            // 1: Nap tien dien thoai (VNPTEpay)
            // 2: Phan mem Viet QR
            // 9: Tat ca

            List<Object> dtos = new ArrayList<>();
            // VNPT-epay
            switch (filterBy) {
                case 1:
                    List<TransactionWalletVNPTEpayDTO> vnptEpays = new ArrayList<>();
                    vnptEpays = new ArrayList<>();
                    // 0: phone no
                    // 1: bill number
                    // 9: tat ca
                    switch (type) {
                        // phone no
                        case 0:
                            vnptEpays = transactionWalletService
                                    .getTransactionWalletByPhoneNoAndVNPTEpay(value, fromDate,
                                            toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletByPhoneNoAndVNPTEpay(value, fromDate,
                                            toDate);
                            break;
                        // bill number
                        case 1:
                            vnptEpays = transactionWalletService
                                    .getTransactionWalletByBillNumberAndVNPTEpay(value, fromDate,
                                            toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletByBillNumberAndVNPTEpay(value, fromDate,
                                            toDate);
                            break;
                        //9: all
                        case 9:
                            vnptEpays = transactionWalletService
                                    .getTransactionWalletVNPTEpay(fromDate,
                                            toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletVNPTEpay(fromDate,
                                            toDate);
                            break;
                        default:
                            break;
                    }
                    dtos = new ArrayList<>();
                    if (vnptEpays != null && !vnptEpays.isEmpty()) {
                        dtos = vnptEpays.stream().map(item -> {
                            TransWalletVNPTEpayAdminDTO dto = new TransWalletVNPTEpayAdminDTO();
                            dto.setId(item.getId());
                            dto.setTimePaid(item.getTimePaid());
                            dto.setAmount(item.getAmount());
                            dto.setBillNumber(item.getBillNumber());
                            dto.setServiceType("Nạp tiền VNPTEpay");
                            dto.setFullName(item.getFullName());
                            dto.setPhoneNo(item.getPhoneNo());
                            dto.setPhoneNorc(item.getPhoneNorc());
                            dto.setEmail(item.getEmail() != null ? item.getEmail() : "");
                            dto.setTimeCreated(item.getTimeCreated());
                            dto.setStatus(item.getStatus());
                            return dto;
                        }).collect(Collectors.toList());
                    }
                    break;
                // 2: Phan mem VietQR (Annual Fee)
                case 2:
                    List<TransactionWalletAdminDTO> response = new ArrayList<>();
                    switch (type) {
                        // 0: Phone no
                        case 0:
                            response = transactionWalletService
                                    .getTransactionWalletByPhoneNoAndAnnualFee
                                            (value, fromDate, toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletByPhoneNoAndAnnualFee
                                            (value, fromDate, toDate);
                            break;
                        // 1: bill number
                        case 1:
                            response = transactionWalletService
                                    .getTransactionWalletByBillNumberAndAnnualFee
                                            (value, fromDate, toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletByBillNumberAndAnnualFee
                                            (value, fromDate, toDate);
                            break;
                        //9: all
                        case 9:
                            response = transactionWalletService
                                    .getTransactionWalletAnnualFee(
                                            fromDate, toDate, offset, size);
                            totalElement = transactionWalletService
                                    .countTransactionWalletAnnualFee
                                            (fromDate, toDate);
                            break;
                        default:
                            break;
                    }
                    dtos = new ArrayList<>();
                    if (response != null && !response.isEmpty()) {
                        dtos = response.stream().map(item -> {
                            TransWalletVqrServiceAdminResDTO dto = new TransWalletVqrServiceAdminResDTO();
                            dto.setId(item.getId());
                            dto.setAmount(item.getAmount());
                            dto.setBillNumber(item.getBillNumber());
                            dto.setStatus(item.getStatus());
                            dto.setTimeCreated(item.getTimeCreated());
                            dto.setTimePaid(item.getTimePaid());
                            dto.setFullName(item.getFullName());
                            dto.setPhoneNo(item.getPhoneNo());
                            
                            return dto;
                        }).collect(Collectors.toList());
                    }
                    break;
                default:
                    break;
            }
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPage(page);
            pageDTO.setSize(size);
            pageDTO.setTotalPage(totalElement % size == 0 ?
                    totalElement / size : totalElement / size + 1);
            pageDTO.setTotalElement(totalElement);
            result.setMetadata(pageDTO);
            data.setItems(dtos);
            result.setData(data);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("getTransactionsVNPTFilter: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            logger.error("getTransactionsVNPTFilter: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
