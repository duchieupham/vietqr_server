package com.vietqr.org.service.redis;

import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.entity.redis.SumUserEntity;
import com.vietqr.org.repository.AccountLoginRepository;
import com.vietqr.org.repository.redis.SumUserRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Service
public class SumOfUserServiceImpl implements SumOfUserService {

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
        SumUserEntity entity = repo.findById(DateTimeUtil.getSimpleDate(date)).orElse(null);
        if (entity == null) {
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
            repo.save(entity);
        }
        return entity;
    }

    @Override
    public SumUserEntity findByAllTime() {
        SumUserEntity entity = repo.findById("ALL_TIME").orElse(null);
        if (entity == null) {
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
            repo.save(entity);
        }
        return entity;
    }
}
