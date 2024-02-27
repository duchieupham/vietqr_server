package com.vietqr.org.service.redis;

import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.entity.redis.SumUserEntity;
import com.vietqr.org.repository.AccountLoginRepository;
import com.vietqr.org.repository.redis.SumUserRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Service
public class SumOfUserServiceImpl implements SumOfUserService {

    private static final Logger logger = Logger.getLogger(SumOfUserServiceImpl.class);

    @Autowired
    private SumUserRepository repo;

    @Autowired
    private AccountLoginRepository accountLoginRepository;

    @Override
    public void save(SumUserEntity entity) {
        repo.save(entity);
    }

    @Override
    public SumUserEntity findByDate(String date) {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Start query redis SumOfUserServiceImpl findByDate: " + now.toInstant(ZoneOffset.UTC));
        SumUserEntity entity = repo.findById(DateTimeUtil.getSimpleDate(date)).orElse(null);
        LocalDateTime end = LocalDateTime.now();
        logger.info("End query reids SumOfUserServiceImpl findByDate: " + end.toInstant(ZoneOffset.UTC));
        logger.info("Sum time redis: " + (Duration.between(end, now)));
        if (entity == null) {
            LocalDateTime nowdb = LocalDateTime.now();
            logger.info("Start query db SumOfUserServiceImpl findByDate: " + nowdb.toInstant(ZoneOffset.UTC));
            entity = new SumUserEntity();
            entity.setId(DateTimeUtil.getSimpleDate(date));
            entity.setDate(date);
            StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
            int sum = accountLoginRepository.sumOfUserByStartDateEndDate(
                    startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                    startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
            entity.setSumOfUser(sum);
            entity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            entity.setDateValue(startEndTimeDTO.getFromDate());
            LocalDateTime enddb = LocalDateTime.now();
            logger.info("End query db SumOfUserServiceImpl findByDate: " + enddb.toInstant(ZoneOffset.UTC));
            logger.info("Sum time db: " + (Duration.between(enddb, nowdb)));
            logger.info("===================================================================");
            repo.save(entity);
        }
        return entity;
    }

    @Override
    public SumUserEntity findByAllTime() {
        LocalDateTime nowrd = LocalDateTime.now();
        logger.info("Start query redis SumOfUserServiceImpl findByAllTime: " + nowrd.toInstant(ZoneOffset.UTC));
        SumUserEntity entity = repo.findById("ALL_TIME").orElse(null);
        LocalDateTime end = LocalDateTime.now();
        logger.info("End query redis SumOfUserServiceImpl findByAllTime: " + end.toInstant(ZoneOffset.UTC));
        logger.info("Sum time db: " + (Duration.between(end, nowrd)));
        if (entity == null) {
            LocalDateTime nowdb = LocalDateTime.now();
            logger.info("Start query db SumOfUserServiceImpl findByAllTime: " + nowdb.toInstant(ZoneOffset.UTC));
            entity = new SumUserEntity();
            entity.setId("ALL_TIME");
            entity.setDate("ALL_TIME");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
            int sum = accountLoginRepository.sumOfUserByStartDateEndDate(0 - DateTimeUtil.GMT_PLUS_7_OFFSET,
                    yesterday.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.GMT_PLUS_7_OFFSET);
            entity.setSumOfUser(sum);
            entity.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            entity.setDateValue(-1);
            LocalDateTime enddb = LocalDateTime.now();
            logger.info("End query db SumOfUserServiceImpl findByAllTime: " + enddb.toInstant(ZoneOffset.UTC));
            logger.info("Sum time db: " + (Duration.between(enddb, nowdb)));

            repo.save(entity);
            logger.info("===================================================================");
        }
        return entity;
    }
}
