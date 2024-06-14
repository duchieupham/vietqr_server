package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantConnectionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantConnectionService {
    List<String> checkExistedCustomerSyncByUsername(String username);

    Integer getCountingMerchantConnection();

    MerchantConnectionEntity createMerchantConnection(MerchantConnectionEntity entity);
}
