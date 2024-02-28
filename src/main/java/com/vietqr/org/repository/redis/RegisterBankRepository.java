package com.vietqr.org.repository.redis;

import com.vietqr.org.entity.redis.RegisterBankEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterBankRepository extends CrudRepository<RegisterBankEntity, String> {
}
