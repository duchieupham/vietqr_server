package com.vietqr.org.service.redis;

import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.dto.SumEachBankDTO;
import com.vietqr.org.entity.redis.StatisticBankEntity;
import com.vietqr.org.entity.redis.SumEachBankEntity;
import com.vietqr.org.repository.AccountBankReceiveRepository;
import com.vietqr.org.repository.redis.StatisticBankRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatisticBankServiceImpl implements StatisticBankService {

    private static final Logger logger = Logger.getLogger(StatisticBankServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AccountBankReceiveRepository accountBankReceiveRepository;

    @Autowired
    private StatisticBankRepository statisticBankRepository;

    @Autowired
    private StatisticBankRepository repo;

    @Override
    public void save(StatisticBankEntity entity) {
        repo.save(entity);
    }

    @Override
    public StatisticBankEntity getAllTime() {
        StatisticBankEntity result = null;
        LocalDateTime nowrd = LocalDateTime.now();
        logger.info("Start query redis StatisticBankServiceImpl getAllTime: " + nowrd.toInstant(ZoneOffset.UTC));
        result = repo.findById("ALL_TIME").orElse(null);
        LocalDateTime endrd = LocalDateTime.now();
        logger.info("End query redis StatisticBankServiceImpl getAllTime: " + endrd.toInstant(ZoneOffset.UTC));
        logger.info("Sum time redis: " + (Duration.between(endrd, nowrd)));

        if (result == null) {
            LocalDateTime nowdb = LocalDateTime.now();
            logger.info("Start query db StatisticBankServiceImpl getAllTime: " + nowdb.toInstant(ZoneOffset.UTC));
            result = new StatisticBankEntity();
            LocalDateTime now = LocalDateTime.now().atZone(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
            String date = DateTimeUtil.getDateString(yesterday);
            result.setTimeValue(DateTimeUtil.getDateAsLongInt(date));
            result.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            result.setId("ALL_TIME");
            result.setDate(date);
            StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
            List<SumEachBankDTO> sumEachBankDTOS = accountBankReceiveRepository
                    .sumEachBank(0 - DateTimeUtil.GMT_PLUS_7_OFFSET,
                            startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
            LocalDateTime enddb = LocalDateTime.now();
            logger.info("End query db StatisticBankServiceImpl getAllTime: " + enddb.toInstant(ZoneOffset.UTC));
            logger.info("Sum time db: " + (Duration.between(enddb, nowdb)));
            List<SumEachBankEntity> sumEachBankEntities = new ArrayList<>();
            sumEachBankEntities = sumEachBankDTOS.stream().map(sumEachBankDTO -> {
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
            result.setSumEachBankEntities(sumEachBankEntities);
            statisticBankRepository.save(result);
        }
        logger.info("===================================================================");
        return result;
    }

    @Override
    public StatisticBankEntity getStatisticBankByDate(String date) {
        StatisticBankEntity result = null;
        String key = DateTimeUtil.getSimpleDate(date);
        LocalDateTime nowrd = LocalDateTime.now();
        logger.info("Start query redis StatisticBankServiceImpl getStatisticBankByDate: " + nowrd.toInstant(ZoneOffset.UTC));
        result = repo.findById(key).orElse(null);
        LocalDateTime endrd = LocalDateTime.now();
        logger.info("End query redis StatisticBankServiceImpl getStatisticBankByDate: " + endrd.toInstant(ZoneOffset.UTC));
        logger.info("Sum time redis: " + (Duration.between(endrd, nowrd)));
        if (result == null) {
            StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
            LocalDateTime nowdb = LocalDateTime.now();
            logger.info("Start query db StatisticBankServiceImpl getStatisticBankByDate: " + nowdb.toInstant(ZoneOffset.UTC));
            List<SumEachBankDTO> sumEachBankDTOS = accountBankReceiveRepository
                    .sumEachBank(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                            startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
            LocalDateTime enddb = LocalDateTime.now();
            logger.info("Start query db StatisticBankServiceImpl getStatisticBankByDate: " + enddb.toInstant(ZoneOffset.UTC));
            logger.info("Sum time db: " + (Duration.between(enddb, nowdb)));
            logger.info("===================================================================");
            List<SumEachBankEntity> sumEachBankEntities = new ArrayList<>();
            sumEachBankEntities = sumEachBankDTOS.stream().map(sumEachBankDTO -> {
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
            statisticBankEntity.setDate(date);
            statisticBankEntity.setTimeValue(DateTimeUtil.getDateAsLongInt(date));
            statisticBankEntity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            statisticBankEntity.setId(DateTimeUtil.getSimpleDate(date));
            statisticBankEntity.setSumEachBankEntities(sumEachBankEntities);

            StatisticBankEntity statisticAllTime = statisticBankRepository
                    .findById("ALL_TIME").orElse(null);
            if (statisticAllTime == null) {
                statisticAllTime = new StatisticBankEntity();
                statisticAllTime.setId("ALL_TIME");
                statisticBankEntity.setDate("ALL_TIME");
                statisticBankEntity.setTimeValue(-1);
                statisticAllTime.setTimeZone(DateTimeUtil.GMT_PLUS_7);
                statisticAllTime.setSumEachBankEntities(sumEachBankEntities);
                statisticBankRepository.save(statisticAllTime);
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
                statisticBankRepository.save(statisticAllTime);
            }
            // save new all time;
        }
        return result;
    }

    @Override
    public void deleteAllRedis() {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
