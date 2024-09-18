package com.vietqr.org.service;

import com.vietqr.org.entity.CustomerErrorLogEntity;
import com.vietqr.org.repository.CustomerErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerErrorLogServiceImpl implements CustomerErrorLogService {

    @Autowired
    private CustomerErrorLogRepository repo;

    @Override
    public String getRetryErrorsByCustomerId(String customerId) {
        return repo.getRetryErrorsByCustomerId(customerId);
    }

    @Override
    public void insertAll(List<CustomerErrorLogEntity> entities) {
        repo.saveAll(entities);
    }
}
