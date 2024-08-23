package com.vietqr.org.service;

import com.vietqr.org.entity.BankReceiveConnectionEntity;
import com.vietqr.org.repository.BankReceiveConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankReceiveConnectionServiceImpl implements BankReceiveConnectionService {

    @Autowired
    private BankReceiveConnectionRepository repo;

    @Override
    public BankReceiveConnectionEntity getBankReceiveConnectionByBankIdAndMid(String bankId, String mid) {
        return repo.getBankReceiveConnectionByBankIdAndMid(bankId, mid);
    }

    @Override
    public void insertAll(List<BankReceiveConnectionEntity> bankReceiveConnectionEntities) {
        repo.saveAll(bankReceiveConnectionEntities);
    }

    @Override
    public List<BankReceiveConnectionEntity> getBankReceiveConnectionByBankId(String bankId) {
        return repo.getBankReceiveConnectionByBankId(bankId);
    }

    @Override
    public String checkBankAccountByBankIdAndMid(String bankId, String mid) {
        return repo.checkBankAccountByBankIdAndMid(bankId, mid);
    }

    @Override
    public void insert(BankReceiveConnectionEntity bankReceiveConnectionEntity) {
        repo.save(bankReceiveConnectionEntity);
    }
}
