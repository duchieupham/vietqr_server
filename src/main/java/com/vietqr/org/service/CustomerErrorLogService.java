package com.vietqr.org.service;

import com.vietqr.org.entity.CustomerErrorLogEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerErrorLogService {
    String getRetryErrorsByCustomerId(String customerId);

    void insertAll(List<CustomerErrorLogEntity> entities);
}
