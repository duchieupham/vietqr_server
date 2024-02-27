package com.vietqr.org.service.redis;

import com.vietqr.org.entity.redis.StatisticUserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StatisticUserService {
    Iterable<StatisticUserEntity> findAll();

    List<StatisticUserEntity> findByDate(String date);
}
