package com.vietqr.org.service;

import com.vietqr.org.dto.BankActiveAdminDataDTO;
import com.vietqr.org.dto.ICheckKeyActiveDTO;
import com.vietqr.org.entity.BankReceiveActiveHistoryEntity;
import com.vietqr.org.repository.BankReceiveActiveHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public BankReceiveActiveHistoryEntity getBankReceiveActiveByUserIdAndBankId(String userId, String bankId) {
        return repo.getBankReceiveActiveByUserIdAndBankId(userId, bankId);
    }

    @Override
    public List<String>  getIdBankReceiveActiveByUserIdAndBankId(String userId, String bankId) {
        return repo.getIdBankReceiveActiveByUserIdAndBankId(userId, bankId);
    }

    @Override
    public List<ICheckKeyActiveDTO> getBankReceiveActiveByUserIdAndBankIdBackUp(String userId, String bankId) {
        return repo.getBankReceiveActiveByUserIdAndBankIdBackUp(userId, bankId);
    }
}
