package com.vietqr.org.service;

import com.vietqr.org.dto.BankActiveAdminDataDTO;
import com.vietqr.org.entity.BankReceiveActiveHistoryEntity;
import org.springframework.stereotype.Service;

@Service
public interface BankReceiveActiveHistoryService {
    void insert(BankReceiveActiveHistoryEntity bankReceiveActiveHistoryEntity);

    BankActiveAdminDataDTO getBankActiveAdminData(String keyActive);
}
