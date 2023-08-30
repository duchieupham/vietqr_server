package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankSmsDTO;
import com.vietqr.org.entity.AccountBankSmsEntity;
import com.vietqr.org.repository.AccountBankSmsRepository;

@Service
public class AccountBankSmsServiceImpl implements AccountBankSmsService {

    @Autowired
    AccountBankSmsRepository repo;

    @Override
    public int insert(AccountBankSmsEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public int insertAll(List<AccountBankSmsEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public List<AccountBankSmsDTO> getListBankAccountSmsBySmsId(String smsId) {
        return repo.getListBankAccountSmsBySmsId(smsId);
    }

    @Override
    public AccountBankSmsDTO getBankAccountSmsById(String id) {
        return repo.getBankAccountSmsById(id);
    }

}
