package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CustomerSyncMappingEntity;

@Service
public interface CustomerSyncMappingService {

    public int insert(CustomerSyncMappingEntity entity);

    public List<CustomerSyncMappingEntity> getCustomerSyncMappingByUserId(String userId);

    public void updateCustomerSyncMapping(String userId, String customerSyncId);
}
