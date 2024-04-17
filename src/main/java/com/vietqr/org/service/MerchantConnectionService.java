package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantConnectionService {
    List<String> checkExistedCustomerSyncByUsername(String username);
}
