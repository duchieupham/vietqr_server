package com.vietqr.org.repository.redis;

import com.vietqr.org.entity.redis.StatisticBankEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticBankRepository extends CrudRepository<StatisticBankEntity, String> {
}
