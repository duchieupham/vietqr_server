package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.redis.StatisticBankEntity;
import com.vietqr.org.entity.redis.SumBankEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.redis.StatisticBankService;
import com.vietqr.org.service.redis.SumOfBankService;
import com.vietqr.org.service.redis.SumOfUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class StatisticController {

    private static final Logger logger = Logger.getLogger(StatisticController.class);

    @Autowired
    private StatisticBankService statisticBankService;

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private SumOfUserService sumOfUserService;

    @Autowired
    private SumOfBankService sumOfBankService;

    @GetMapping("statistic-user")
    public ResponseEntity<List<RegisterUserResponseDTO>> getStatisticUser(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int offset) {
        List<RegisterUserResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        // type
        // 0: phone
        // 1: fullName
        // 2: registerPlatform
        // 9: all
        try {
            switch (type) {
                case 0:
                    result = accountLoginService.getAllRegisterUserResponseByPhone(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 1:
                    result = accountLoginService.getAllRegisterUserResponseByName(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 2:
                    result = accountLoginService.getAllRegisterUserResponseByPlatform(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 9:
                    result = accountLoginService.getAllRegisterUserResponse(fromDate, toDate, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("statistic-sum-of-user")
    public ResponseEntity<Object> getSumOfUser(@RequestParam String date) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (date.trim().isEmpty()) {
                result = sumOfUserService.findByAllTime();
            } else {
                result = sumOfUserService.findByDate(date);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("statistic-bank")
    public ResponseEntity<List<RegisterBankResponseDTO>> getListBank(@RequestParam String fromDate,
                                                                     @RequestParam String toDate,
                                                                     @RequestParam(defaultValue = "9") int type,
                                                                     @RequestParam(required = false) String value,
                                                                     @RequestParam int offset) {
        // type
        // 0: phone
        // 1: bankTypeId
        // 2: bankAccount
        // 3: bankName
        // 9: all
        List<RegisterBankResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            switch (type) {
                case 0:
                    result = accountBankReceiveService.getListBankByDateAndPhone(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 1:
                    result = accountBankReceiveService.getListBankByDateAndBankTypeId(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 2:
                    result = accountBankReceiveService.getListBankByDateAndBankAccount(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 3:
                    result = accountBankReceiveService.getListBankByDateAndBankName(fromDate, toDate, value, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                case 9:
                    result = accountBankReceiveService.getListBankByDate(fromDate, toDate, offset);
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("statistic-sum-of-bank")
    public ResponseEntity<SumBankEntity> getSumOfBank(
            @RequestParam String date) {
        SumBankEntity result = null;
        HttpStatus httpStatus = null;
        try {
            if (date.trim().isEmpty()) {
                result = sumOfBankService.findByAllTime();
            } else {
                result = sumOfBankService.findByDate(date);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("statistic-bank-date")
    public ResponseEntity<StatisticBankEntity> getStatisticBank(
            @RequestParam String date
    ) {
        StatisticBankEntity result = null;
        HttpStatus httpStatus = null;
        try {
            if (date.trim().isEmpty()) {
                result = statisticBankService.getAllTime();
            } else {
                result = statisticBankService.getStatisticBankByDate(date);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("statistic-remove-redis")
    public ResponseEntity<ResponseMessageDTO> removeAllKey() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            statisticBankService.deleteAllRedis();
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
