package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.redis.RegisterBankEntity;
import com.vietqr.org.entity.redis.StatisticBankEntity;
import com.vietqr.org.entity.redis.StatisticUserEntity;
import com.vietqr.org.entity.redis.SumBankEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.redis.*;
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
    private RegisterBankService registerBankService;

    @Autowired
    private StatisticUserService statisticUserService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private SumOfUserService sumOfUserService;

    @Autowired
    private SumOfBankService sumOfBankService;

    @GetMapping("statistic-user")
    public ResponseEntity<Iterable<StatisticUserEntity>> getStatisticUser() {
        Iterable<StatisticUserEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = statisticUserService.findAll();
            httpStatus = HttpStatus.OK;
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

    @GetMapping("statistic-user-previous")
    public ResponseEntity<Iterable<StatisticUserEntity>> getPreviousUser() {
        Iterable<StatisticUserEntity> result = null;
        HttpStatus httpStatus = null;
        try {
            result = statisticUserService.findAll();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("statistic-user-previous")
    public ResponseEntity<List<StatisticUserEntity>> getPreviousUser(@RequestParam String date) {
        List<StatisticUserEntity> result = null;
        HttpStatus httpStatus = null;
        try {
            result = statisticUserService.findByDate(date);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("statistic-bank")
    public ResponseEntity<List<RegisterBankResponseDTO>> getListBank() {
        List<RegisterBankResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<RegisterBankEntity> registerBankEntities = registerBankService.findAll();
//            result = accountBankReceiveService.getListBank();
            httpStatus = HttpStatus.OK;
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
