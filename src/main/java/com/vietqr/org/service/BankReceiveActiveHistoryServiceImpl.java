package com.vietqr.org.service;

import com.vietqr.org.dto.BankActiveAdminDataDTO;
import com.vietqr.org.entity.BankReceiveActiveHistoryEntity;
import com.vietqr.org.repository.BankReceiveActiveHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankReceiveActiveHistoryServiceImpl implements BankReceiveActiveHistoryService {

    @Autowired
    private BankReceiveActiveHistoryRepository repo;

    @Override
    public void insert(BankReceiveActiveHistoryEntity entity) {
        repo.save(entity);
    }

    @Override
    public BankActiveAdminDataDTO getBankActiveAdminData(String keyActive) {
        return repo.getBankActiveAdminData(keyActive);
    }
}
