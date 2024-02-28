package com.vietqr.org.service.redis;

import com.vietqr.org.dto.RegisterUserDTO;
import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.entity.redis.StatisticUserEntity;
import com.vietqr.org.repository.AccountLoginRepository;
import com.vietqr.org.repository.redis.StatisticUserRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticUserServiceImpl implements StatisticUserService {

    private static final Logger logger = Logger.getLogger(StatisticUserServiceImpl.class);

    @Autowired
    private StatisticUserRepository repo;

    @Autowired
    private AccountLoginRepository accountLoginRepository;

    @Override
    public Iterable<StatisticUserEntity> findAll() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Start query redis StatisticUserEntity findByDate: " + now.toInstant(ZoneOffset.UTC));
        Iterable<StatisticUserEntity> result = repo.findAll();
        LocalDateTime end = LocalDateTime.now();
        logger.info("End query redis StatisticUserEntity findByDate: " + end.toInstant(ZoneOffset.UTC));
        logger.info("Sum time redis: " + (Duration.between(end, now)));
        logger.info("===================================================================");
        return result;
    }

    @Override
    public List<StatisticUserEntity> findByDate(String date) {
        repo.deleteAll();
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
        LocalDateTime now = LocalDateTime.now();
        logger.info("Start query database StatisticUserEntity findByDate: " + now.toInstant(ZoneOffset.UTC));
        List<RegisterUserDTO> result = accountLoginRepository.getAllRegisterUser(
                startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
        List<StatisticUserEntity> list = result.stream().map(item -> {
            StatisticUserEntity entity = new StatisticUserEntity();
            entity.setDate(date);
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
            entity.setRegisterDate(startEndTimeDTO.getFromDate());
            return entity;
        }).collect(Collectors.toList());
        LocalDateTime end = LocalDateTime.now();
        logger.info("End query database StatisticUserEntity findByDate: " + end.toInstant(ZoneOffset.UTC));
        logger.info("Sum time db: " + (Duration.between(now, end)));
        repo.saveAll(list);
        logger.info("===================================================================");
        return list;

    }

    @Override
    public void removeAll() {
        repo.deleteAll();
    }

    @Override
    public void saveAll(List<StatisticUserEntity> list) {
        repo.saveAll(list);
    }
}
