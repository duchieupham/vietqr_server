package com.vietqr.org.service;

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
}
