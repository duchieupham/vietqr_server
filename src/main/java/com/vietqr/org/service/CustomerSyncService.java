package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CustomerSyncEntity;

@Service
public interface CustomerSyncService {

    public int insertCustomerSync(CustomerSyncEntity entity);

    public List<CustomerSyncEntity> getCustomerSyncEntities();

    public CustomerSyncEntity getCustomerSyncById(String id);

}
