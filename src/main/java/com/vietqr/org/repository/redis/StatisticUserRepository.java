package com.vietqr.org.repository.redis;

import com.vietqr.org.entity.redis.StatisticUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticUserRepository extends CrudRepository<StatisticUserEntity, String> {
}
