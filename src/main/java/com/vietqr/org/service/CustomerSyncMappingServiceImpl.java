package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CustomerSyncMappingEntity;
import com.vietqr.org.repository.CustomerSyncMappingRepository;

@Service
public class CustomerSyncMappingServiceImpl implements CustomerSyncMappingService {

    @Autowired
    CustomerSyncMappingRepository repo;

    @Override
    public int insert(CustomerSyncMappingEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<CustomerSyncMappingEntity> getCustomerSyncMappingByUserId(String userId) {
        return repo.getCustomerSyncMappingByUserId(userId);
    }

    @Override
    public void updateCustomerSyncMapping(String userId, String customerSyncId) {
        repo.updateCustomerSyncMapping(customerSyncId, userId);
    }

}
