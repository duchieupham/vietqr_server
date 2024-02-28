package com.vietqr.org.repository.redis;

import com.vietqr.org.entity.redis.SumUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SumUserRepository extends CrudRepository<SumUserEntity, String> {
}
