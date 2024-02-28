package com.vietqr.org.service.redis;

import com.vietqr.org.entity.redis.SumUserEntity;
import org.springframework.stereotype.Service;

@Service
public interface SumOfUserService {
    void save(SumUserEntity entity);
    SumUserEntity findByDate(String date);
    SumUserEntity findByAllTime();
}
