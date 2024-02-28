package com.vietqr.org.service.redis;

import com.vietqr.org.entity.redis.SumBankEntity;
import org.springframework.stereotype.Service;

@Service
public interface SumOfBankService {
    void save(SumBankEntity sumBankEntity);

    SumBankEntity findByAllTime();

    SumBankEntity findByDate(String id);
}
