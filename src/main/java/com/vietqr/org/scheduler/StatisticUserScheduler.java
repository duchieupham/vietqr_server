package com.vietqr.org.scheduler;

import com.vietqr.org.dto.SumEachBankDTO;
import com.vietqr.org.dto.SumOfBankDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StatisticUserScheduler {

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private StatisticBankService statisticBankService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private SumOfBankService sumOfBankService;

    @Autowired
    private SumOfUserService sumOfUserService;

    @Scheduled(zone = "Asia/Ho_Chi_Minh", cron = "0 0 0 * * *")
    public void sumOfUserPreviousDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
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
        LocalDateTime yesterday = now.minusDays(1);
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
        LocalDateTime yesterday = now.minusDays(1);
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
}
