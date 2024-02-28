package com.vietqr.org.scheduler;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.redis.*;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.redis.*;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StatisticUserScheduler {

    private static final Logger logger = Logger.getLogger(StatisticUserScheduler.class);

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private StatisticBankService statisticBankService;

    @Autowired
    private RegisterBankService registerBankService;

    @Autowired
    private StatisticUserService statisticUserService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private SumOfBankService sumOfBankService;

    @Autowired
    private SumOfUserService sumOfUserService;

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void sumOfUserPreviousDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
        int sumOfUser = accountLoginService.sumOfUserByStartDateEndDate(DateTimeUtil.getDateString(yesterday));
        SumUserEntity sumUserEntity = new SumUserEntity();
        sumUserEntity.setDate(DateTimeUtil.getDateString(yesterday));
        sumUserEntity.setSumOfUser(sumOfUser);
        sumUserEntity.setDateValue(yesterday.with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC));
        sumUserEntity.setId(DateTimeUtil.getSimpleDate(DateTimeUtil.getDateString(yesterday)));
        sumUserEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
        sumOfUserService.save(sumUserEntity);

        // save new all time;
        SumUserEntity sumOfUserAllTime = sumOfUserService.findByAllTime();
        sumOfUserAllTime.setSumOfUser(sumOfUserAllTime.getSumOfUser() + sumOfUser);
        sumOfUserService.save(sumOfUserAllTime);
    }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void sumOfBankPreviousDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
        SumOfBankDTO sumOfBank = accountBankReceiveService
                .sumOfBankByStartDateEndDate(DateTimeUtil.getDateString(yesterday));
        SumBankEntity sumBankEntity = new SumBankEntity();
        sumBankEntity.setDate(DateTimeUtil.getDateString(yesterday));
        sumBankEntity.setNumberOfBankNotAuthenticated(sumOfBank.getNumberOfBankNotAuthenticated());
        sumBankEntity.setNumberOfBankAuthenticated(sumOfBank.getNumberOfBankAuthenticated());
        sumBankEntity.setNumberOfBank(sumOfBank.getNumberOfBank());
        sumBankEntity.setId(DateTimeUtil.getSimpleDate(DateTimeUtil.getDateString(yesterday)));
        sumBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
        sumOfBankService.save(sumBankEntity);

        // save new all time;
        SumBankEntity sumBankEntityAllTime = sumOfBankService.findByDate("ALL_TIME");
        sumBankEntityAllTime.setNumberOfBankNotAuthenticated(
                sumBankEntityAllTime.getNumberOfBankNotAuthenticated()
                + sumOfBank.getNumberOfBankNotAuthenticated());
        sumBankEntityAllTime.setNumberOfBankAuthenticated(sumBankEntityAllTime.getNumberOfBankAuthenticated()
                + sumOfBank.getNumberOfBankAuthenticated());
        sumBankEntityAllTime.setNumberOfBank(sumBankEntityAllTime.getNumberOfBank()
                + sumOfBank.getNumberOfBank());
        sumOfBankService.save(sumBankEntityAllTime);
    }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void sumOfEachBankPreviousDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
        List<SumEachBankDTO> sumEachBankDTOS = accountBankReceiveService
                .sumEachBank(DateTimeUtil.getDateString(yesterday));
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
        statisticBankEntity.setDate(DateTimeUtil.getDateString(yesterday));
        statisticBankEntity.setTimeValue(yesterday.with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC));
        statisticBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
        statisticBankEntity.setId(DateTimeUtil.getSimpleDate(DateTimeUtil.getDateString(yesterday)));
        statisticBankEntity.setSumEachBankEntities(sumEachBankEntities);

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
        // save new all time;
    }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void setStatisticUserEveryDay() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            statisticUserService.removeAll();
            List<RegisterUserDTO> result = accountLoginService.getAllRegisterUser(DateTimeUtil.getDateString(yesterday));
            List<StatisticUserEntity> list = result.stream().map(item -> {
                StatisticUserEntity entity = new StatisticUserEntity();
                entity.setDate(DateTimeUtil.getDateString(yesterday));
                entity.setDateValue(item.getTime());
                entity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                entity.setId(item.getId());
                entity.setUserId(item.getId());
                entity.setPhoneNo(item.getPhoneNo());
                entity.setEmail(item.getEmail());
                entity.setFullName(item.getFullName());
                entity.setAddress(item.getAddress());
                entity.setIpAddress(item.getUserIp());
                entity.setRegisterPlatform(item.getRegisterPlatform());
                entity.setRegisterDate(item.getTime());
                return entity;
            }).collect(Collectors.toList());
            statisticUserService.saveAll(list);
        } catch (Exception e) {
            logger.error("Error setStatisticUserEveryDay: ", e);
        }
    }

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void setStatisticBankAccountEveryDay() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            registerBankService.removeAll();
            List<RegisterBankResponseDTO> result = accountBankReceiveService.getListBankByDate(DateTimeUtil.getDateString(yesterday));
            List<RegisterBankEntity> list = result.stream().map(item -> {
                RegisterBankEntity entity = new RegisterBankEntity();
                entity.setId(item.getId());
                entity.setBankName(item.getBankName());
                entity.setBankAccount(item.getBankAccount());
                entity.setBankAccountName(item.getBankAccountName());
                entity.setBankShortName(item.getBankShortName());
                entity.setBankCode(item.getBankCode());
                entity.setIsAuthenticated(item.getIsAuthenticated());
                entity.setNationId(item.getNationId());
                entity.setPhoneAuthenticated(item.getPhoneAuthenticated());
                entity.setUserId(item.getUserId());
                entity.setPhoneAccount(item.getPhoneAccount());
                entity.setDate(DateTimeUtil.getDateString(yesterday));
                entity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                entity.setTimeValue(yesterday.with(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC));
                return entity;
            }).collect(Collectors.toList());
            registerBankService.saveAll(list);
        } catch (Exception e) {
            logger.error("Error setStatisticBankAccountEveryDay: ", e);
        }
    }
}
