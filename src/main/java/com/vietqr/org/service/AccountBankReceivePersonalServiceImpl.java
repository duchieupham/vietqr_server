package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankReceivePersonalDTO;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.repository.AccountBankReceivePersonalRepository;

@Service
public class AccountBankReceivePersonalServiceImpl implements AccountBankReceivePersonalService {

    @Autowired
    AccountBankReceivePersonalRepository repo;

    @Override
    public int insertAccountBankReceivePersonal(BankReceivePersonalEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<AccountBankReceivePersonalDTO> getBankReceivePersonals(String userId) {
        return repo.getPersonalBankReceive(userId);
    }

}
