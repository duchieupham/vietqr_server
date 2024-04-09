package com.vietqr.org.service;

import com.vietqr.org.entity.TransReceiveTempEntity;
import org.springframework.stereotype.Service;

@Service
public interface TransReceiveTempService {
    TransReceiveTempEntity getLastTimeByBankId(String bankId);

    void insert(TransReceiveTempEntity entity);
}
