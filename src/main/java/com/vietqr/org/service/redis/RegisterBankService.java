package com.vietqr.org.service.redis;

import com.vietqr.org.dto.RegisterBankResponseDTO;
import com.vietqr.org.entity.redis.RegisterBankEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegisterBankService {
    void removeAll();

    void saveAll(List<RegisterBankEntity> registerBankEntities);

    List<RegisterBankEntity> findAll();
}
