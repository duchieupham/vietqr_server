package com.vietqr.org.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankFeeDateUpdateDTO;
import com.vietqr.org.dto.AccountBankFeeInsertDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankFeeEntity;
import com.vietqr.org.entity.ServiceFeeEntity;
import com.vietqr.org.service.AccountBankFeeService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.ServiceFeeService;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AccountBankFeeController {
    private static final Logger logger = Logger.getLogger(ServiceFeeController.class);

    @Autowired
    AccountBankFeeService accountBankFeeService;

    @Autowired
    ServiceFeeService serviceFeeService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    // TWO types insert:
    // 0. add merchant - all bankID belong to merchant apply fee
    // 1. add bank - bank apply fee
    @PostMapping("bank/service-fee")
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
                                // check percent fee OR transFee -> add into fee acc bank

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
                        // insert bankfee
                        accountBankFeeService.insert(entity);
                        // check annual fee -> add into annual acc bank
                        if (serviceFeeEntity.getAnnualFee() != 0 && serviceFeeEntity.getMonthlyCycle() != 0) {

                        }
                        // check percent fee OR transFee -> add into fee acc bank

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

    // update duration fee service - bank
    @PostMapping("bank/service-fee/date")
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
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
