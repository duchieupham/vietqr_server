package com.vietqr.org.service.redis;

import com.vietqr.org.entity.redis.StatisticBankEntity;

public interface StatisticBankService {
    public void save(StatisticBankEntity entity);

    public StatisticBankEntity getAllTime();

    public StatisticBankEntity getStatisticBankByDate(String date);

    void deleteAllRedis();
}
