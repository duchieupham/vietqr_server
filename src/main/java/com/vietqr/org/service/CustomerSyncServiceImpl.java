package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.repository.CustomerSyncRepository;

@Service
public class CustomerSyncServiceImpl implements CustomerSyncService {

    @Autowired
    CustomerSyncRepository repo;

    @Override
    public int insertCustomerSync(CustomerSyncEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<CustomerSyncEntity> getCustomerSyncEntities() {
        return repo.getCustomerSyncEntities();
    }

}
