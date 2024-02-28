package com.vietqr.org.repository.redis;

import com.vietqr.org.entity.redis.SumBankEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SumBankRepository extends CrudRepository<SumBankEntity, String> {
}
