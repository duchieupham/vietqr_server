package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.redis.StatisticBankEntity;
import com.vietqr.org.entity.redis.SumBankEntity;
import com.vietqr.org.entity.redis.SumEachBankEntity;
import com.vietqr.org.entity.redis.SumUserEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.redis.StatisticBankService;
import com.vietqr.org.service.redis.SumOfBankService;
import com.vietqr.org.service.redis.SumOfUserService;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping("statistic-sum-of-bank")
    public ResponseEntity<ResponseMessageDTO> saveSumOfBank() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            long numberBank = 0;
            long numberBankAuthenticated = 0;
            long numberBankNotAuthenticated = 0;
            List<String> dateList = accountBankReceiveService.getAllDate();
            for (String dateValue : dateList) {
                SumOfBankDTO sumOfBankDTO = accountBankReceiveService.sumOfBankByStartDateEndDate(dateValue);
                SumBankEntity sumBankEntity = new SumBankEntity();
                sumBankEntity.setDate(dateValue);
                sumBankEntity.setNumberOfBankNotAuthenticated(sumOfBankDTO.getNumberOfBankNotAuthenticated());
                sumBankEntity.setNumberOfBankAuthenticated(sumOfBankDTO.getNumberOfBankAuthenticated());
                sumBankEntity.setNumberOfBank(sumOfBankDTO.getNumberOfBank());
                sumBankEntity.setId(DateTimeUtil.getSimpleDate(dateValue));
                sumBankEntity.setTimeValue(DateTimeUtil.getDateAsLongInt(dateValue));
                sumBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                sumOfBankService.save(sumBankEntity);
                numberBank += sumOfBankDTO.getNumberOfBank();
                numberBankAuthenticated += sumOfBankDTO.getNumberOfBankAuthenticated();
                numberBankNotAuthenticated += sumOfBankDTO.getNumberOfBankNotAuthenticated();
            }
            SumBankEntity sumBankEntity = new SumBankEntity();
            sumBankEntity.setDate("ALL_TIME");
            sumBankEntity.setNumberOfBankNotAuthenticated(numberBankNotAuthenticated);
            sumBankEntity.setNumberOfBankAuthenticated(numberBankAuthenticated);
            sumBankEntity.setNumberOfBank(numberBank);
            sumBankEntity.setId("ALL_TIME");
            sumBankEntity.setTimeValue(0);
            sumBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            sumOfBankService.save(sumBankEntity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("statistic-bank")
    public ResponseEntity<ResponseMessageDTO> saveStatisticBank() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> dateList = accountBankReceiveService.getAllDate();
            for (String dateValue : dateList) {
                List<SumEachBankDTO> sumEachBankDTOS = accountBankReceiveService
                        .sumEachBank(dateValue);
                List<SumEachBankEntity> sumEachBankEntities = sumEachBankDTOS.stream().map(sumEachBankDTO -> {
                    SumEachBankEntity sumEachBankEntity = new SumEachBankEntity();
                    sumEachBankEntity.setBankCode(sumEachBankDTO.getBankCode());
                    sumEachBankEntity.setBankName(sumEachBankDTO.getBankName());
                    sumEachBankEntity.setBankShortName(sumEachBankDTO.getBankShortName());
                    sumEachBankEntity.setBankTypeId(sumEachBankDTO.getBankTypeId());
                    sumEachBankEntity.setImgId(sumEachBankDTO.getImgId());
                    sumEachBankEntity.setNumberOfBank(sumEachBankDTO.getNumberOfBank());
                    sumEachBankEntity.setNumberOfBankAuthenticated(sumEachBankDTO.getNumberOfBankAuthenticated());
                    sumEachBankEntity.setNumberOfBankUnauthenticated(sumEachBankDTO.getNumberOfBankNotAuthenticated());
                    return sumEachBankEntity;
                }).collect(Collectors.toList());

                StatisticBankEntity statisticBankEntity = new StatisticBankEntity();
                statisticBankEntity.setDate(dateValue);
                statisticBankEntity.setTimeValue(DateTimeUtil.getDateAsLongInt(dateValue));
                statisticBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                statisticBankEntity.setId(DateTimeUtil.getSimpleDate(dateValue));
                statisticBankEntity.setSumEachBankEntities(sumEachBankEntities);
                statisticBankService.save(statisticBankEntity);
                statisticBankService.delete("ALL_TIME");
                StatisticBankEntity statisticAllTime = statisticBankService
                        .getAllTime();
                if (statisticAllTime == null) {
                    statisticAllTime = new StatisticBankEntity();
                    statisticAllTime.setId("ALL_TIME");
                    statisticBankEntity.setDate("ALL_TIME");
                    statisticBankEntity.setTimeValue(-1);
                    statisticAllTime.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                    statisticAllTime.setSumEachBankEntities(sumEachBankEntities);
                    statisticBankService.save(statisticAllTime);
                } else {
                    // thống kê hien co trong statistic bank
                    List<SumEachBankEntity> sumEachBankEntitiesAllTime = statisticAllTime.getSumEachBankEntities();
                    // Loop through sumEachBankEntities
                    for (SumEachBankEntity sumEachBankEntity : sumEachBankEntities) {
                        boolean found = false;

                        // Loop through sumEachBankEntitiesAllTime to check if bankTypeId exists
                        for (SumEachBankEntity sumEachBankEntityAllTime : sumEachBankEntitiesAllTime) {
                            if (Objects.equals(sumEachBankEntity.getBankTypeId(), sumEachBankEntityAllTime.getBankTypeId())) {
                                // If bankTypeId exists, update the sums
                                sumEachBankEntityAllTime.setNumberOfBank(sumEachBankEntityAllTime.getNumberOfBank() + sumEachBankEntity.getNumberOfBank());
                                sumEachBankEntityAllTime.setNumberOfBankAuthenticated(sumEachBankEntityAllTime.getNumberOfBankAuthenticated() + sumEachBankEntity.getNumberOfBankAuthenticated());
                                sumEachBankEntityAllTime.setNumberOfBankUnauthenticated(sumEachBankEntityAllTime.getNumberOfBankUnauthenticated() + sumEachBankEntity.getNumberOfBankUnauthenticated());
                                found = true;
                                break;
                            }
                        }

                        // If bankTypeId doesn't exist, create a new record
                        if (!found) {
                            SumEachBankEntity newSumEachBankEntity = new SumEachBankEntity();
                            newSumEachBankEntity.setBankCode(sumEachBankEntity.getBankCode());
                            newSumEachBankEntity.setBankName(sumEachBankEntity.getBankName());
                            newSumEachBankEntity.setBankShortName(sumEachBankEntity.getBankShortName());
                            newSumEachBankEntity.setBankTypeId(sumEachBankEntity.getBankTypeId());
                            newSumEachBankEntity.setImgId(sumEachBankEntity.getImgId());
                            newSumEachBankEntity.setNumberOfBank(sumEachBankEntity.getNumberOfBank());
                            newSumEachBankEntity.setNumberOfBankAuthenticated(sumEachBankEntity.getNumberOfBankAuthenticated());
                            newSumEachBankEntity.setNumberOfBankUnauthenticated(sumEachBankEntity.getNumberOfBankUnauthenticated());
                            sumEachBankEntitiesAllTime.add(newSumEachBankEntity);
                        }
                    }
                    statisticAllTime.setSumEachBankEntities(sumEachBankEntitiesAllTime);
                    statisticBankService.save(statisticAllTime);
                }
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("statistic-sum-of-user")
    public ResponseEntity<ResponseMessageDTO> saveSumOfUser() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> dateList = accountLoginService.getAllDate();
            for (String dateValue : dateList) {
                int sumOfUser = accountLoginService.sumOfUserByStartDateEndDate(dateValue);
                SumUserEntity sumUserEntity = new SumUserEntity();
                sumUserEntity.setDate(dateValue);
                sumUserEntity.setSumOfUser(sumOfUser);
                sumUserEntity.setDateValue(DateTimeUtil.getDateTimeAsLongInt(dateValue));
                sumUserEntity.setId(DateTimeUtil.getSimpleDate(dateValue));
                sumUserEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                sumOfUserService.save(sumUserEntity);
            }
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "");
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
