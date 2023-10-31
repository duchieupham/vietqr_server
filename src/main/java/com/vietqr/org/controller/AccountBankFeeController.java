package com.vietqr.org.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankFeeDateUpdateDTO;
import com.vietqr.org.dto.AccountBankFeeInsertDTO;
import com.vietqr.org.dto.AnnualFeeBankDTO;
import com.vietqr.org.dto.AnnualFeeBankItemDTO;
import com.vietqr.org.dto.AnnualFeeItemDTO;
import com.vietqr.org.dto.AnnualFeeMerchantDTO;
import com.vietqr.org.dto.AnnualFeeMerchantItemDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.ServiceFeeBankItemDTO;
import com.vietqr.org.dto.ServiceFeeMerchantItemDTO;
import com.vietqr.org.dto.ServiceFeeMonthItemDTO;
import com.vietqr.org.dto.TotalPaymentDTO;
import com.vietqr.org.dto.TransactionFeeDTO;
import com.vietqr.org.entity.AccountBankFeeEntity;
import com.vietqr.org.entity.PaymentAnnualAccBankEntity;
import com.vietqr.org.entity.PaymentFeeAccBankEntity;
import com.vietqr.org.entity.ServiceFeeEntity;
import com.vietqr.org.service.AccountBankFeeService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.PaymentAnnualAccBankService;
import com.vietqr.org.service.PaymentFeeAccBankService;
import com.vietqr.org.service.ServiceFeeService;
import com.vietqr.org.service.TransactionReceiveService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankFeeController {
    private static final Logger logger = Logger.getLogger(ServiceFeeController.class);

    @Autowired
    AccountBankFeeService accountBankFeeService;

    @Autowired
    ServiceFeeService serviceFeeService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    PaymentAnnualAccBankService paymentAnnualAccBankService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    PaymentFeeAccBankService paymentFeeAccBankService;

    // TWO types insert:
    // 0. add merchant - all bankID belong to merchant apply fee
    // 1. add bank - bank apply fee
    @PostMapping("admin/bank/service-fee")
    public ResponseEntity<ResponseMessageDTO> insertBankFee(
            @RequestBody AccountBankFeeInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getInsertType() == 0) {
                    // type = 0 -> add merchant
                    // find bankIds belong to merchant
                    // get detail service fee by id
                    // insert bankfee
                    List<String> bankIds = accountCustomerBankService
                            .getBankIdsByCustomerSyncId(dto.getCustomerSyncId());
                    if (bankIds != null && !bankIds.isEmpty()) {
                        ServiceFeeEntity serviceFeeEntity = serviceFeeService.getServiceFeeById(dto.getServiceFeeId());
                        if (serviceFeeEntity != null) {
                            for (String bankId : bankIds) {
                                AccountBankFeeEntity entity = new AccountBankFeeEntity();
                                UUID uuid = UUID.randomUUID();
                                entity.setId(uuid.toString());
                                entity.setActiveFee(serviceFeeEntity.getActiveFee());
                                entity.setAnnualFee(serviceFeeEntity.getAnnualFee());
                                entity.setBankId(bankId);
                                entity.setMonthlyCycle(serviceFeeEntity.getMonthlyCycle());
                                entity.setPercentFee(serviceFeeEntity.getPercentFee());
                                entity.setServiceFeeId(dto.getServiceFeeId());
                                entity.setShortName(serviceFeeEntity.getShortName());
                                entity.setTransFee(serviceFeeEntity.getTransFee());
                                entity.setCountingTransType(serviceFeeEntity.getCountingTransType());
                                entity.setStartDate(calculateStartDate());
                                entity.setEndDate(
                                        calculateEndDate(calculateStartDate(), serviceFeeEntity.getMonthlyCycle()));
                                entity.setVat(serviceFeeEntity.getVat());
                                // insert bankfee
                                accountBankFeeService.insert(entity);
                                // check annual fee -> add into annual acc bank
                                PaymentAnnualAccBankEntity entity2 = new PaymentAnnualAccBankEntity();
                                UUID uuid2 = UUID.randomUUID();
                                entity2.setId(uuid2.toString());
                                entity2.setBankId(bankId);
                                entity2.setAccountBankFeeId(uuid.toString());
                                entity2.setFromDate(calculateStartDate());
                                entity2.setToDate(
                                        calculateEndDate(calculateStartDate(), serviceFeeEntity.getMonthlyCycle()));
                                // get annual fee with VAT
                                Long totalPayment = 0L;
                                Long totalPaymentAfterVAT = 0L;
                                totalPayment = serviceFeeEntity.getAnnualFee() * serviceFeeEntity.getMonthlyCycle();
                                totalPaymentAfterVAT = (Long) Math
                                        .round(totalPayment
                                                + (serviceFeeEntity.getVat() * totalPayment) / 100);
                                entity2.setTotalPayment(totalPaymentAfterVAT);
                                // 0: Unpaid
                                // 1: paid
                                entity2.setStatus(0);
                                paymentAnnualAccBankService.insert(entity2);

                                result = new ResponseMessageDTO("SUCCESS", "");
                                httpStatus = HttpStatus.OK;
                            }
                        } else {
                            logger.error("insertBankFee: SERVICE FEE NOT FOUND");
                            result = new ResponseMessageDTO("FAILED", "E90");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error("insertBankFee: BANKS NOT FOUND");
                        result = new ResponseMessageDTO("FAILED", "E91");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else if (dto.getInsertType() == 1) {
                    // type = 1 -> add bank
                    // get detail service fee by id
                    ServiceFeeEntity serviceFeeEntity = serviceFeeService.getServiceFeeById(dto.getServiceFeeId());
                    if (serviceFeeEntity != null) {
                        AccountBankFeeEntity entity = new AccountBankFeeEntity();
                        UUID uuid = UUID.randomUUID();
                        entity.setId(uuid.toString());
                        entity.setActiveFee(serviceFeeEntity.getActiveFee());
                        entity.setAnnualFee(serviceFeeEntity.getAnnualFee());
                        entity.setBankId(dto.getBankId());
                        entity.setMonthlyCycle(serviceFeeEntity.getMonthlyCycle());
                        entity.setPercentFee(serviceFeeEntity.getPercentFee());
                        entity.setServiceFeeId(dto.getServiceFeeId());
                        entity.setShortName(serviceFeeEntity.getShortName());
                        entity.setTransFee(serviceFeeEntity.getTransFee());
                        entity.setCountingTransType(serviceFeeEntity.getCountingTransType());
                        entity.setStartDate(calculateStartDate());
                        entity.setEndDate(calculateEndDate(calculateStartDate(), serviceFeeEntity.getMonthlyCycle()));
                        entity.setVat(serviceFeeEntity.getVat());
                        // insert bankfee
                        accountBankFeeService.insert(entity);
                        // check annual fee -> add into annual acc bank
                        PaymentAnnualAccBankEntity entity2 = new PaymentAnnualAccBankEntity();
                        UUID uuid2 = UUID.randomUUID();
                        entity2.setId(uuid2.toString());
                        entity2.setBankId(dto.getBankId());
                        entity2.setAccountBankFeeId(uuid.toString());
                        entity2.setFromDate(calculateStartDate());
                        entity2.setToDate(calculateEndDate(calculateStartDate(), serviceFeeEntity.getMonthlyCycle()));
                        // get annual fee with VAT
                        Long totalPayment = 0L;
                        Long totalPaymentAfterVAT = 0L;
                        totalPayment = serviceFeeEntity.getAnnualFee() * serviceFeeEntity.getMonthlyCycle();
                        totalPaymentAfterVAT = (Long) Math
                                .round(totalPayment
                                        + (serviceFeeEntity.getVat() * totalPayment) / 100);
                        entity2.setTotalPayment(totalPaymentAfterVAT);
                        // 0: Unpaid
                        // 1: paid
                        entity2.setStatus(0);
                        paymentAnnualAccBankService.insert(entity2);
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        logger.error("insertBankFee: SERVICE FEE NOT FOUND");
                        result = new ResponseMessageDTO("FAILED", "E90");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }

                } else {
                    logger.error("insertBankFee: INVALID INSERT TYPE");
                    result = new ResponseMessageDTO("FAILED", "E89");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertBankFee: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public String calculateStartDate() {
        try {
            // Lấy thời gian hiện tại (UTC)
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

            // Chuyển đổi sang múi giờ UTC+7
            ZonedDateTime currentDateTimeUTC7 = currentDateTime.withZoneSameInstant(ZoneId.of("UTC+7"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return currentDateTimeUTC7.format(formatter);
        } catch (Exception e) {
            return "";
        }
    }

    public String calculateEndDate(String startDateString, int durationMonths) {
        try {
            // Chuyển đổi chuỗi startDate thành ZonedDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateString, formatter);

            // Thêm số tháng vào startDate
            LocalDate endDate = startDate.plusMonths(durationMonths);

            return endDate.format(formatter);
        } catch (Exception e) {
            return "";
        }
    }

    // get annual list by merchant
    @GetMapping("admin/merchant/service-fee/annual")
    public ResponseEntity<List<AnnualFeeMerchantItemDTO>> getAnnualFeeList() {
        List<AnnualFeeMerchantItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<AnnualFeeMerchantDTO> merchants = customerSyncService.getMerchantForServiceFee();
            if (merchants != null && !merchants.isEmpty()) {
                //
                for (AnnualFeeMerchantDTO merchant : merchants) {
                    AnnualFeeMerchantItemDTO dto = new AnnualFeeMerchantItemDTO();
                    List<AnnualFeeBankItemDTO> bankItems = new ArrayList<>();
                    int status = 1;
                    boolean allStatusOne = true; // Biến cờ đánh dấu
                    Long totalPayment = 0L;
                    dto.setCustomerSyncId(merchant.getCustomerSyncId());
                    dto.setMerchant(merchant.getMerchant());
                    //
                    if (merchant.getCustomerSyncId() != null) {

                        List<AnnualFeeBankDTO> banks = accountCustomerBankService
                                .getBanksAnnualFee(merchant.getCustomerSyncId());
                        if (banks != null && !banks.isEmpty()) {
                            //
                            for (AnnualFeeBankDTO bank : banks) {
                                AnnualFeeBankItemDTO bankItem = new AnnualFeeBankItemDTO();
                                bankItem.setBankId(bank.getBankId());
                                bankItem.setBankAccount(bank.getBankAccount());
                                bankItem.setBankCode(bank.getBankCode());
                                bankItem.setBankShortName(bank.getBankShortName());
                                //
                                List<AnnualFeeItemDTO> feeItems = paymentAnnualAccBankService
                                        .getAnnualFeesByBankId(bank.getBankId());
                                if (feeItems != null && !feeItems.isEmpty()) {
                                    for (AnnualFeeItemDTO feeItem : feeItems) {
                                        totalPayment += feeItem.getTotalPayment();
                                        if (feeItem.getStatus() != null && feeItem.getStatus() != 1) {
                                            allStatusOne = false;
                                        } else if (feeItem.getStatus() == null) {
                                            allStatusOne = false;
                                        }
                                    }
                                    bankItem.setFees(feeItems);
                                    bankItems.add(bankItem);
                                }
                            }
                        }
                    }
                    ////
                    dto.setTotalPayment(totalPayment);
                    if (!allStatusOne) {
                        status = 0; // Nếu biến cờ không được đánh dấu, gán giá trị 0 cho status
                    }
                    dto.setStatus(status);
                    if (bankItems != null && !bankItems.isEmpty()) {
                        dto.setBankAccounts(bankItems);
                        result.add(dto);
                    }

                }
            }
            httpStatus = HttpStatus.OK;
        } catch (

        Exception e) {
            logger.error("getAnnualFeeList: ERROR: " + e.toString());
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // 6 giây
    @GetMapping("admin/merchant/service-fee/transaction")
    public ResponseEntity<List<ServiceFeeMerchantItemDTO>> getServiceFeeList(
            @RequestParam(value = "month") String month) {
        List<ServiceFeeMerchantItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<AnnualFeeMerchantDTO> merchants = customerSyncService.getMerchantForServiceFee();
            if (merchants != null && !merchants.isEmpty()) {
                List<CompletableFuture<ServiceFeeMerchantItemDTO>> futures = new ArrayList<>();

                for (AnnualFeeMerchantDTO merchant : merchants) {
                    CompletableFuture<ServiceFeeMerchantItemDTO> future = CompletableFuture.supplyAsync(() -> {
                        ServiceFeeMerchantItemDTO dto = new ServiceFeeMerchantItemDTO();
                        // Thực hiện xử lý thông tin phí dịch vụ của merchant tại đây
                        List<ServiceFeeBankItemDTO> bankItems = new ArrayList<>();
                        int status = 1;
                        boolean allStatusOne = true;
                        Long totalPayment = 0L;
                        dto.setCustomerSyncId(merchant.getCustomerSyncId());
                        dto.setMerchant(merchant.getMerchant());
                        //
                        if (merchant.getCustomerSyncId() != null) {

                            List<AnnualFeeBankDTO> banks = accountCustomerBankService
                                    .getBanksAnnualFee(merchant.getCustomerSyncId());
                            if (banks != null && !banks.isEmpty()) {

                                for (AnnualFeeBankDTO bank : banks) {
                                    ServiceFeeBankItemDTO bankItem = new ServiceFeeBankItemDTO();
                                    bankItem.setBankId(bank.getBankId());
                                    bankItem.setBankAccount(bank.getBankAccount());
                                    bankItem.setBankCode(bank.getBankCode());
                                    bankItem.setBankShortName(bank.getBankShortName());
                                    //
                                    List<ServiceFeeMonthItemDTO> feeItems = new ArrayList<>();
                                    List<AccountBankFeeEntity> fees = accountBankFeeService
                                            .getAccountBankFeesByBankId(bank.getBankId());
                                    if (fees != null && !fees.isEmpty()) {
                                        for (AccountBankFeeEntity fee : fees) {
                                            ServiceFeeMonthItemDTO feeItem = new ServiceFeeMonthItemDTO();
                                            feeItem.setAccountBankFeeId(fee.getId());
                                            feeItem.setServiceFeeId(fee.getServiceFeeId());
                                            feeItem.setShortName(fee.getShortName());
                                            feeItem.setVat(fee.getVat());
                                            feeItem.setCountingTransType(fee.getCountingTransType());
                                            feeItem.setDiscountAmount(fee.getAnnualFee());
                                            //
                                            Long totalTrans = 0L;
                                            Long totalAmount = 0L;
                                            Long totalPaymentService = 0L;
                                            Long totalPaymentServiceAfterVat = 0L;
                                            Long totalPaymentServiceByTransFee = 0L;
                                            Long totalPaymentServiceByPercentAmount = 0L;
                                            int statusFee = 0;
                                            // getCountingTransType = 0 => count all
                                            // getCountingTransType = 1 => count only system transaction
                                            if (fee.getCountingTransType() != null && fee.getCountingTransType() == 0) {
                                                TransactionFeeDTO transFee = transactionReceiveService
                                                        .getTransactionFeeCountingTypeAll(fee.getBankId(), month);
                                                if (transFee != null) {
                                                    totalTrans = transFee.getTotalTrans();
                                                    totalAmount = transFee.getTotalAmount();
                                                    //
                                                    totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                    totalPaymentServiceByPercentAmount = (Long) Math
                                                            .round(fee.getPercentFee() * totalAmount / 100);
                                                    totalPaymentService = totalPaymentServiceByTransFee
                                                            + totalPaymentServiceByPercentAmount;
                                                    totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                            + (fee.getVat() * totalPaymentService / 100)
                                                            - fee.getAnnualFee());
                                                    if (totalPaymentServiceAfterVat < 0) {
                                                        totalPaymentServiceAfterVat = 0L;
                                                    }
                                                    //
                                                    feeItem.setTotalTrans(totalTrans);
                                                    feeItem.setTotalAmount(totalAmount);
                                                    feeItem.setTotalPayment(totalPaymentServiceAfterVat);
                                                    // check status
                                                    PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                            .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                    if (checkPaid != null && checkPaid.getStatus() != null) {
                                                        statusFee = checkPaid.getStatus();
                                                    } else {
                                                        statusFee = 0;
                                                    }
                                                    feeItem.setStatus(statusFee);
                                                    //
                                                    feeItems.add(feeItem);
                                                }
                                            } else if (fee.getCountingTransType() != null
                                                    && fee.getCountingTransType() == 1) {
                                                TransactionFeeDTO transFee = transactionReceiveService
                                                        .getTransactionFeeCountingTypeSystem(fee.getBankId(), month);
                                                if (transFee != null) {
                                                    totalTrans = transFee.getTotalTrans();
                                                    totalAmount = transFee.getTotalAmount();
                                                    //
                                                    totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                    totalPaymentServiceByPercentAmount = (Long) Math
                                                            .round(fee.getPercentFee() * totalAmount / 100);
                                                    totalPaymentService = totalPaymentServiceByTransFee
                                                            + totalPaymentServiceByPercentAmount;
                                                    totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                            + (fee.getVat() * totalPaymentService / 100)
                                                            - fee.getAnnualFee());
                                                    if (totalPaymentServiceAfterVat < 0) {
                                                        totalPaymentServiceAfterVat = 0L;
                                                    }
                                                    //
                                                    feeItem.setTotalTrans(totalTrans);
                                                    feeItem.setTotalAmount(totalAmount);
                                                    feeItem.setTotalPayment(totalPaymentServiceAfterVat);
                                                    // check status
                                                    PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                            .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                    if (checkPaid != null && checkPaid.getStatus() != null) {
                                                        statusFee = checkPaid.getStatus();
                                                    } else {
                                                        statusFee = 0;
                                                    }
                                                    feeItem.setStatus(statusFee);
                                                    //
                                                    feeItems.add(feeItem);
                                                }
                                            }

                                        }
                                    }
                                    //
                                    if (feeItems != null && !feeItems.isEmpty()) {
                                        for (ServiceFeeMonthItemDTO feeItem : feeItems) {
                                            totalPayment += feeItem.getTotalPayment();
                                            if (feeItem.getStatus() != null && feeItem.getStatus() != 1) {
                                                allStatusOne = false;
                                            } else if (feeItem.getStatus() == null) {
                                                allStatusOne = false;
                                            }
                                        }
                                        bankItem.setFees(feeItems);
                                        bankItems.add(bankItem);
                                    }
                                }
                            }

                        }
                        dto.setTotalPayment(totalPayment);
                        if (!allStatusOne) {
                            status = 0; // Nếu biến cờ không được đánh dấu, gán giá trị 0 cho status
                        }
                        dto.setStatus(status);
                        if (bankItems != null && !bankItems.isEmpty()) {
                            dto.setBankAccounts(bankItems);
                            result.add(dto);
                        }
                        return dto;
                    });
                    futures.add(future);
                }

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                // Chờ tất cả các CompletableFuture hoàn thành
                allFutures.join();

                for (CompletableFuture<ServiceFeeMerchantItemDTO> future : futures) {
                    try {
                        ServiceFeeMerchantItemDTO dto = future.getNow(null);
                        if (dto != null && dto.getBankAccounts() != null && !dto.getBankAccounts().isEmpty()) {
                            result.add(dto);
                        }
                    } catch (Exception e) {
                        // Xử lý ngoại lệ nếu có
                    }
                }
            }

            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            // Xử lý ngoại lệ nếu có
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    // @GetMapping("admin/merchant/service-fee/transaction")
    // public ResponseEntity<List<ServiceFeeMerchantItemDTO>> getServiceFeeList(
    // @RequestParam("month") String month) {
    // try {
    // List<AnnualFeeMerchantDTO> merchants =
    // customerSyncService.getMerchantForServiceFee();
    // List<CompletableFuture<ServiceFeeMerchantItemDTO>> futures =
    // merchants.stream()
    // .map(merchant -> CompletableFuture.supplyAsync(() ->
    // processMerchant(merchant, month)))
    // .collect(Collectors.toList());

    // CompletableFuture<Void> allFutures =
    // CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    // allFutures.join();

    // List<ServiceFeeMerchantItemDTO> result = futures.stream()
    // .map(CompletableFuture::join)
    // .filter(Objects::nonNull)
    // .collect(Collectors.toList());

    // return ResponseEntity.ok(result);
    // } catch (Exception e) {
    // // Xử lý ngoại lệ nếu có
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    // private ServiceFeeMerchantItemDTO processMerchant(AnnualFeeMerchantDTO
    // merchant, String month) {
    // ServiceFeeMerchantItemDTO dto = new ServiceFeeMerchantItemDTO();
    // // Thực hiện xử lý thông tin phí dịch vụ của merchant tại đây
    // List<ServiceFeeBankItemDTO> bankItems = new ArrayList<>();
    // int status = 1;
    // boolean allStatusOne = true;
    // Long totalPayment = 0L;
    // dto.setCustomerSyncId(merchant.getCustomerSyncId());
    // dto.setMerchant(merchant.getMerchant());

    // if (merchant.getCustomerSyncId() != null) {
    // List<AnnualFeeBankDTO> banks =
    // accountCustomerBankService.getBanksAnnualFee(merchant.getCustomerSyncId());
    // for (AnnualFeeBankDTO bank : banks) {
    // ServiceFeeBankItemDTO bankItem = new ServiceFeeBankItemDTO();
    // bankItem.setBankId(bank.getBankId());
    // bankItem.setBankAccount(bank.getBankAccount());
    // bankItem.setBankCode(bank.getBankCode());
    // bankItem.setBankShortName(bank.getBankShortName());

    // List<ServiceFeeMonthItemDTO> feeItems = new ArrayList<>();
    // List<AccountBankFeeEntity> fees =
    // accountBankFeeService.getAccountBankFeesByBankId(bank.getBankId());
    // for (AccountBankFeeEntity fee : fees) {
    // ServiceFeeMonthItemDTO feeItem = new ServiceFeeMonthItemDTO();
    // feeItem.setAccountBankFeeId(fee.getId());
    // feeItem.setServiceFeeId(fee.getServiceFeeId());
    // feeItem.setShortName(fee.getShortName());
    // feeItem.setVat(fee.getVat());
    // feeItem.setCountingTransType(fee.getCountingTransType());
    // feeItem.setDiscountAmount(fee.getAnnualFee());

    // Long totalTrans = 0L;
    // Long totalAmount = 0L;
    // Long totalPaymentService = 0L;
    // Long totalPaymentServiceAfterVat = 0L;
    // Long totalPaymentServiceByTransFee = 0L;
    // Long totalPaymentServiceByPercentAmount = 0L;
    // int statusFee = 0;

    // if (fee.getCountingTransType() != null && fee.getCountingTransType() == 0) {
    // TransactionFeeDTO transFee =
    // transactionReceiveService.getTransactionFeeCountingTypeAll(
    // fee.getBankId(), month);
    // if (transFee != null) {
    // totalTrans = transFee.getTotalTrans();
    // totalAmount = transFee.getTotalAmount();

    // totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
    // totalPaymentServiceByPercentAmount = Math.round(fee.getPercentFee() *
    // totalAmount / 100);
    // totalPaymentService = totalPaymentServiceByTransFee +
    // totalPaymentServiceByPercentAmount;
    // totalPaymentServiceAfterVat = Math.round(totalPaymentService
    // + (fee.getVat() * totalPaymentService / 100) - fee.getAnnualFee());

    // if (totalPaymentServiceAfterVat < 0) {
    // totalPaymentServiceAfterVat = 0L;
    // }

    // feeItem.setTotalTrans(totalTrans);
    // feeItem.setTotalAmount(totalAmount);
    // feeItem.setTotalPayment(totalPaymentServiceAfterVat);

    // PaymentFeeAccBankEntity checkPaid =
    // paymentFeeAccBankService.checkExistedRecord(
    // fee.getBankId(), fee.getId(), month);
    // statusFee = (checkPaid != null && checkPaid.getStatus() != null) ?
    // checkPaid.getStatus()
    // : 0;
    // feeItem.setStatus(statusFee);
    // feeItems.add(feeItem);
    // }
    // }
    // }

    // bankItem.setFees(feeItems);
    // bankItems.add(bankItem);

    // if (bankItems.size() > 0) {
    // dto.setBankAccounts(bankItems);
    // }
    // }
    // }

    // return dto;
    // }

    // OLD 45 giây
    // @GetMapping("admin/merchant/service-fee/transaction2")
    // public ResponseEntity<List<ServiceFeeMerchantItemDTO>> getServiceFeeList2(
    // @RequestParam(value = "month") String month) {
    // List<ServiceFeeMerchantItemDTO> result = new ArrayList<>();
    // HttpStatus httpStatus = null;
    // try {
    // List<AnnualFeeMerchantDTO> merchants =
    // customerSyncService.getMerchantForServiceFee();
    // if (merchants != null && !merchants.isEmpty()) {
    // //
    // for (AnnualFeeMerchantDTO merchant : merchants) {
    // ServiceFeeMerchantItemDTO dto = new ServiceFeeMerchantItemDTO();
    // List<ServiceFeeBankItemDTO> bankItems = new ArrayList<>();
    // int status = 1;
    // boolean allStatusOne = true;
    // Long totalPayment = 0L;
    // dto.setCustomerSyncId(merchant.getCustomerSyncId());
    // dto.setMerchant(merchant.getMerchant());
    // //
    // if (merchant.getCustomerSyncId() != null) {
    // List<AnnualFeeBankDTO> banks = accountCustomerBankService
    // .getBanksAnnualFee(merchant.getCustomerSyncId());
    // if (banks != null && !banks.isEmpty()) {
    // for (AnnualFeeBankDTO bank : banks) {
    // ServiceFeeBankItemDTO bankItem = new ServiceFeeBankItemDTO();
    // bankItem.setBankId(bank.getBankId());
    // bankItem.setBankAccount(bank.getBankAccount());
    // bankItem.setBankCode(bank.getBankCode());
    // bankItem.setBankShortName(bank.getBankShortName());
    // //
    // List<ServiceFeeMonthItemDTO> feeItems = new ArrayList<>();
    // List<AccountBankFeeEntity> fees = accountBankFeeService
    // .getAccountBankFeesByBankId(bank.getBankId());
    // if (fees != null && !fees.isEmpty()) {
    // for (AccountBankFeeEntity fee : fees) {
    // ServiceFeeMonthItemDTO feeItem = new ServiceFeeMonthItemDTO();
    // feeItem.setAccountBankFeeId(fee.getId());
    // feeItem.setServiceFeeId(fee.getServiceFeeId());
    // feeItem.setShortName(fee.getShortName());
    // feeItem.setVat(fee.getVat());
    // feeItem.setCountingTransType(fee.getCountingTransType());
    // feeItem.setDiscountAmount(fee.getAnnualFee());
    // //
    // Long totalTrans = 0L;
    // Long totalAmount = 0L;
    // Long totalPaymentService = 0L;
    // Long totalPaymentServiceAfterVat = 0L;
    // Long totalPaymentServiceByTransFee = 0L;
    // Long totalPaymentServiceByPercentAmount = 0L;
    // int statusFee = 0;
    // // getCountingTransType = 0 => count all
    // // getCountingTransType = 1 => count only system transaction
    // if (fee.getCountingTransType() != null && fee.getCountingTransType() == 0) {
    // TransactionFeeDTO transFee = transactionReceiveService
    // .getTransactionFeeCountingTypeAll(fee.getBankId(), month);
    // if (transFee != null) {
    // totalTrans = transFee.getTotalTrans();
    // totalAmount = transFee.getTotalAmount();
    // //
    // totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
    // totalPaymentServiceByPercentAmount = (Long) Math
    // .round(fee.getPercentFee() * totalAmount / 100);
    // totalPaymentService = totalPaymentServiceByTransFee
    // + totalPaymentServiceByPercentAmount;
    // totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
    // + (fee.getVat() * totalPaymentService / 100)
    // - fee.getAnnualFee());
    // if (totalPaymentServiceAfterVat < 0) {
    // totalPaymentServiceAfterVat = 0L;
    // }
    // //
    // feeItem.setTotalTrans(totalTrans);
    // feeItem.setTotalAmount(totalAmount);
    // feeItem.setTotalPayment(totalPaymentServiceAfterVat);
    // // check status
    // PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
    // .checkExistedRecord(fee.getBankId(), fee.getId(), month);
    // if (checkPaid != null && checkPaid.getStatus() != null) {
    // statusFee = checkPaid.getStatus();
    // } else {
    // statusFee = 0;
    // }
    // feeItem.setStatus(statusFee);
    // //
    // feeItems.add(feeItem);
    // }
    // } else if (fee.getCountingTransType() != null
    // && fee.getCountingTransType() == 1) {
    // TransactionFeeDTO transFee = transactionReceiveService
    // .getTransactionFeeCountingTypeSystem(fee.getBankId(), month);
    // if (transFee != null) {
    // totalTrans = transFee.getTotalTrans();
    // totalAmount = transFee.getTotalAmount();
    // //
    // totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
    // totalPaymentServiceByPercentAmount = (Long) Math
    // .round(fee.getPercentFee() * totalAmount / 100);
    // totalPaymentService = totalPaymentServiceByTransFee
    // + totalPaymentServiceByPercentAmount;
    // totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
    // + (fee.getVat() * totalPaymentService / 100)
    // - fee.getAnnualFee());
    // if (totalPaymentServiceAfterVat < 0) {
    // totalPaymentServiceAfterVat = 0L;
    // }
    // //
    // feeItem.setTotalTrans(totalTrans);
    // feeItem.setTotalAmount(totalAmount);
    // feeItem.setTotalPayment(totalPaymentServiceAfterVat);
    // // check status
    // PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
    // .checkExistedRecord(fee.getBankId(), fee.getId(), month);
    // if (checkPaid != null && checkPaid.getStatus() != null) {
    // statusFee = checkPaid.getStatus();
    // } else {
    // statusFee = 0;
    // }
    // feeItem.setStatus(statusFee);
    // //
    // feeItems.add(feeItem);
    // }
    // }

    // }
    // }
    // //
    // if (feeItems != null && !feeItems.isEmpty()) {
    // for (ServiceFeeMonthItemDTO feeItem : feeItems) {
    // totalPayment += feeItem.getTotalPayment();
    // if (feeItem.getStatus() != null && feeItem.getStatus() != 1) {
    // allStatusOne = false;
    // } else if (feeItem.getStatus() == null) {
    // allStatusOne = false;
    // }
    // }
    // bankItem.setFees(feeItems);
    // bankItems.add(bankItem);
    // }
    // }
    // }
    // }
    // dto.setTotalPayment(totalPayment);
    // if (!allStatusOne) {
    // status = 0; // Nếu biến cờ không được đánh dấu, gán giá trị 0 cho status
    // }
    // dto.setStatus(status);
    // if (bankItems != null && !bankItems.isEmpty()) {
    // dto.setBankAccounts(bankItems);
    // result.add(dto);
    // }
    // }
    // }
    // httpStatus = HttpStatus.OK;
    // } catch (Exception e) {
    // logger.error("getServiceFeeList: ERROR: " + e.toString());
    // }
    // return new ResponseEntity<>(result, httpStatus);
    // }

    // update status annual fee

    // update status transaction fee

    // update duration fee service - bank
    @PostMapping("admin/bank/service-fee/date")
    public ResponseEntity<ResponseMessageDTO> updateApplyDate(
            @RequestBody AccountBankFeeDateUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                // type == 0 => update start date
                // type == 1 => update end date
                if (dto.getType() == 0) {
                    accountBankFeeService.updateStartDate(dto.getDate(), dto.getAccountBankFeeId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else if (dto.getType() == 1) {
                    accountBankFeeService.udpateEndDate(dto.getDate(), dto.getAccountBankFeeId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateApplyDate: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get total transaction and total Payment by month
    @GetMapping("admin/bank/service-fee/total")
    public ResponseEntity<TotalPaymentDTO> getTotalByMonth(
            @RequestParam(value = "month") String month) {
        TotalPaymentDTO result = null;
        HttpStatus httpStatus = null;
        Long totalPaymentForAll = 0L;
        Long totalPaymentUnpaid = 0L;
        Long totalPaymentPaid = 0L;
        Long totalTransForAll = 0L;
        try {
            List<AnnualFeeMerchantDTO> merchants = customerSyncService.getMerchantForServiceFee();
            if (merchants != null && !merchants.isEmpty()) {
                //
                for (AnnualFeeMerchantDTO merchant : merchants) {
                    if (merchant.getCustomerSyncId() != null) {
                        List<AnnualFeeBankDTO> banks = accountCustomerBankService
                                .getBanksAnnualFee(merchant.getCustomerSyncId());
                        if (banks != null && !banks.isEmpty()) {
                            for (AnnualFeeBankDTO bank : banks) {
                                List<AccountBankFeeEntity> fees = accountBankFeeService
                                        .getAccountBankFeesByBankId(bank.getBankId());
                                if (fees != null && !fees.isEmpty()) {
                                    for (AccountBankFeeEntity fee : fees) {

                                        Long totalTrans = 0L;
                                        Long totalAmount = 0L;
                                        Long totalPaymentService = 0L;
                                        Long totalPaymentServiceAfterVat = 0L;
                                        Long totalPaymentServiceByTransFee = 0L;
                                        Long totalPaymentServiceByPercentAmount = 0L;
                                        int statusFee = 0;
                                        // getCountingTransType = 0 => count all
                                        // getCountingTransType = 1 => count only system transaction
                                        if (fee.getCountingTransType() != null && fee.getCountingTransType() == 0) {
                                            TransactionFeeDTO transFee = transactionReceiveService
                                                    .getTransactionFeeCountingTypeAll(fee.getBankId(), month);
                                            if (transFee != null) {
                                                totalTrans = transFee.getTotalTrans();
                                                totalAmount = transFee.getTotalAmount();
                                                //
                                                totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                totalPaymentServiceByPercentAmount = (Long) Math
                                                        .round(fee.getPercentFee() * totalAmount / 100);
                                                totalPaymentService = totalPaymentServiceByTransFee
                                                        + totalPaymentServiceByPercentAmount;
                                                totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                        + (fee.getVat() * totalPaymentService / 100)
                                                        - fee.getAnnualFee());
                                                if (totalPaymentServiceAfterVat < 0) {
                                                    totalPaymentServiceAfterVat = 0L;
                                                }

                                                // check status
                                                PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                        .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                if (checkPaid != null && checkPaid.getStatus() != null) {
                                                    statusFee = checkPaid.getStatus();
                                                } else {
                                                    statusFee = 0;
                                                }
                                                ///
                                                if (statusFee == 0) {
                                                    totalPaymentUnpaid += totalPaymentServiceAfterVat;
                                                } else if (statusFee == 1) {
                                                    totalPaymentPaid += totalPaymentServiceAfterVat;
                                                }
                                                totalTransForAll += totalTrans;
                                                totalPaymentForAll += totalPaymentServiceAfterVat;
                                            }
                                        } else if (fee.getCountingTransType() != null
                                                && fee.getCountingTransType() == 1) {
                                            TransactionFeeDTO transFee = transactionReceiveService
                                                    .getTransactionFeeCountingTypeSystem(fee.getBankId(), month);
                                            if (transFee != null) {
                                                totalTrans = transFee.getTotalTrans();
                                                totalAmount = transFee.getTotalAmount();
                                                //
                                                totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                totalPaymentServiceByPercentAmount = (Long) Math
                                                        .round(fee.getPercentFee() * totalAmount / 100);
                                                totalPaymentService = totalPaymentServiceByTransFee
                                                        + totalPaymentServiceByPercentAmount;
                                                totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                        + (fee.getVat() * totalPaymentService / 100)
                                                        - fee.getAnnualFee());
                                                if (totalPaymentServiceAfterVat < 0) {
                                                    totalPaymentServiceAfterVat = 0L;
                                                }

                                                // check status
                                                PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                        .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                if (checkPaid != null && checkPaid.getStatus() != null) {
                                                    statusFee = checkPaid.getStatus();
                                                } else {
                                                    statusFee = 0;
                                                }

                                                ///
                                                if (statusFee == 0) {
                                                    totalPaymentUnpaid += totalPaymentServiceAfterVat;
                                                } else if (statusFee == 1) {
                                                    totalPaymentPaid += totalPaymentServiceAfterVat;
                                                }
                                                totalTransForAll += totalTrans;
                                                totalPaymentForAll += totalPaymentServiceAfterVat;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
            //
            result = new TotalPaymentDTO();
            result.setTotalTrans(totalTransForAll);
            result.setTotalPayment(totalPaymentForAll);
            result.setTotalPaymentUnpaid(totalPaymentUnpaid);
            result.setTotalPaymentPaid(totalPaymentPaid);
            //
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTotalByMonth: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //
    @GetMapping("bank/service-fee/transaction")
    public ResponseEntity<List<ServiceFeeMerchantItemDTO>> getServiceFeeListByCustomerSyncId(
            @RequestParam(value = "customerSyncId") String customerSyncId,
            @RequestParam(value = "month") String month) {
        List<ServiceFeeMerchantItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (customerSyncId != null && !customerSyncId.trim().isEmpty()) {
                List<AnnualFeeMerchantDTO> merchants = customerSyncService.getMerchantForServiceFeeById(customerSyncId);
                if (merchants != null && !merchants.isEmpty()) {
                    //
                    for (AnnualFeeMerchantDTO merchant : merchants) {
                        ServiceFeeMerchantItemDTO dto = new ServiceFeeMerchantItemDTO();
                        List<ServiceFeeBankItemDTO> bankItems = new ArrayList<>();
                        int status = 1;
                        boolean allStatusOne = true;
                        Long totalPayment = 0L;
                        dto.setCustomerSyncId(merchant.getCustomerSyncId());
                        dto.setMerchant(merchant.getMerchant());
                        //
                        if (merchant.getCustomerSyncId() != null) {
                            List<AnnualFeeBankDTO> banks = accountCustomerBankService
                                    .getBanksAnnualFee(merchant.getCustomerSyncId());
                            if (banks != null && !banks.isEmpty()) {
                                for (AnnualFeeBankDTO bank : banks) {
                                    ServiceFeeBankItemDTO bankItem = new ServiceFeeBankItemDTO();
                                    bankItem.setBankId(bank.getBankId());
                                    bankItem.setBankAccount(bank.getBankAccount());
                                    bankItem.setBankCode(bank.getBankCode());
                                    bankItem.setBankShortName(bank.getBankShortName());
                                    //
                                    List<ServiceFeeMonthItemDTO> feeItems = new ArrayList<>();
                                    List<AccountBankFeeEntity> fees = accountBankFeeService
                                            .getAccountBankFeesByBankId(bank.getBankId());
                                    if (fees != null && !fees.isEmpty()) {
                                        for (AccountBankFeeEntity fee : fees) {
                                            ServiceFeeMonthItemDTO feeItem = new ServiceFeeMonthItemDTO();
                                            feeItem.setAccountBankFeeId(fee.getId());
                                            feeItem.setServiceFeeId(fee.getServiceFeeId());
                                            feeItem.setShortName(fee.getShortName());
                                            feeItem.setVat(fee.getVat());
                                            feeItem.setCountingTransType(fee.getCountingTransType());
                                            feeItem.setDiscountAmount(fee.getAnnualFee());
                                            //
                                            Long totalTrans = 0L;
                                            Long totalAmount = 0L;
                                            Long totalPaymentService = 0L;
                                            Long totalPaymentServiceAfterVat = 0L;
                                            Long totalPaymentServiceByTransFee = 0L;
                                            Long totalPaymentServiceByPercentAmount = 0L;
                                            int statusFee = 0;
                                            // getCountingTransType = 0 => count all
                                            // getCountingTransType = 1 => count only system transaction
                                            if (fee.getCountingTransType() != null && fee.getCountingTransType() == 0) {
                                                TransactionFeeDTO transFee = transactionReceiveService
                                                        .getTransactionFeeCountingTypeAll(fee.getBankId(), month);
                                                if (transFee != null) {
                                                    totalTrans = transFee.getTotalTrans();
                                                    totalAmount = transFee.getTotalAmount();
                                                    //
                                                    totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                    totalPaymentServiceByPercentAmount = (Long) Math
                                                            .round(fee.getPercentFee() * totalAmount / 100);
                                                    totalPaymentService = totalPaymentServiceByTransFee
                                                            + totalPaymentServiceByPercentAmount;
                                                    totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                            + (fee.getVat() * totalPaymentService / 100)
                                                            - fee.getAnnualFee());
                                                    if (totalPaymentServiceAfterVat < 0) {
                                                        totalPaymentServiceAfterVat = 0L;
                                                    }
                                                    //
                                                    feeItem.setTotalTrans(totalTrans);
                                                    feeItem.setTotalAmount(totalAmount);
                                                    feeItem.setTotalPayment(totalPaymentServiceAfterVat);
                                                    // check status
                                                    PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                            .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                    if (checkPaid != null && checkPaid.getStatus() != null) {
                                                        statusFee = checkPaid.getStatus();
                                                    } else {
                                                        statusFee = 0;
                                                    }
                                                    feeItem.setStatus(statusFee);
                                                    //
                                                    feeItems.add(feeItem);
                                                }
                                            } else if (fee.getCountingTransType() != null
                                                    && fee.getCountingTransType() == 1) {
                                                TransactionFeeDTO transFee = transactionReceiveService
                                                        .getTransactionFeeCountingTypeSystem(fee.getBankId(), month);
                                                if (transFee != null) {
                                                    totalTrans = transFee.getTotalTrans();
                                                    totalAmount = transFee.getTotalAmount();
                                                    //
                                                    totalPaymentServiceByTransFee = totalTrans * fee.getTransFee();
                                                    totalPaymentServiceByPercentAmount = (Long) Math
                                                            .round(fee.getPercentFee() * totalAmount / 100);
                                                    totalPaymentService = totalPaymentServiceByTransFee
                                                            + totalPaymentServiceByPercentAmount;
                                                    totalPaymentServiceAfterVat = (Long) Math.round(totalPaymentService
                                                            + (fee.getVat() * totalPaymentService / 100)
                                                            - fee.getAnnualFee());
                                                    if (totalPaymentServiceAfterVat < 0) {
                                                        totalPaymentServiceAfterVat = 0L;
                                                    }
                                                    //
                                                    feeItem.setTotalTrans(totalTrans);
                                                    feeItem.setTotalAmount(totalAmount);
                                                    feeItem.setTotalPayment(totalPaymentServiceAfterVat);
                                                    // check status
                                                    PaymentFeeAccBankEntity checkPaid = paymentFeeAccBankService
                                                            .checkExistedRecord(fee.getBankId(), fee.getId(), month);
                                                    if (checkPaid != null && checkPaid.getStatus() != null) {
                                                        statusFee = checkPaid.getStatus();
                                                    } else {
                                                        statusFee = 0;
                                                    }
                                                    feeItem.setStatus(statusFee);
                                                    //
                                                    feeItems.add(feeItem);
                                                }
                                            }

                                        }
                                    }
                                    //
                                    if (feeItems != null && !feeItems.isEmpty()) {
                                        for (ServiceFeeMonthItemDTO feeItem : feeItems) {
                                            totalPayment += feeItem.getTotalPayment();
                                            if (feeItem.getStatus() != null && feeItem.getStatus() != 1) {
                                                allStatusOne = false;
                                            } else if (feeItem.getStatus() == null) {
                                                allStatusOne = false;
                                            }
                                        }
                                        bankItem.setFees(feeItems);
                                        bankItems.add(bankItem);
                                    }
                                }
                            }
                        }
                        dto.setTotalPayment(totalPayment);
                        if (!allStatusOne) {
                            status = 0; // Nếu biến cờ không được đánh dấu, gán giá trị 0 cho status
                        }
                        dto.setStatus(status);
                        if (bankItems != null && !bankItems.isEmpty()) {
                            dto.setBankAccounts(bankItems);
                            result.add(dto);
                        }
                    }
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getServiceFeeListByCustomerSyncId: ERROR: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("getServiceFeeList: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
