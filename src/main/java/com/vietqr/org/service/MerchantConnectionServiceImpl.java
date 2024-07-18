package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantConnectionEntity;
import com.vietqr.org.repository.MerchantConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantConnectionServiceImpl implements MerchantConnectionService {
    @Autowired
    MerchantConnectionRepository repo;
    @Override
    public List<String> checkExistedCustomerSyncByUsername(String username) {
        return repo.checkExistedCustomerSyncByUsername(username);
    }

    @Override
    public Integer getCountingMerchantConnection() {
        return repo.getCountingMerchantConnection();
    }

    @Override
    public MerchantConnectionEntity createMerchantConnection(MerchantConnectionEntity entity) {
        return repo.save(entity);
    }

    @Override
    public List<String> getIdMerchantConnectionByMid(String mid) {
        return repo.getIdMerchantConnectionByMid(mid);
    }

    @Override
    public MerchantConnectionEntity getMerchanConnectionById(String id) {
        return repo.getMerchanConnectionById(id);
    }
}
