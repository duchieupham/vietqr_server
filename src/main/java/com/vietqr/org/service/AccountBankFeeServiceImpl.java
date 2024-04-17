package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankFeeInfoDTO;
import com.vietqr.org.entity.AccountBankFeeEntity;
import com.vietqr.org.repository.AccountbankFeeRepository;

@Service
public class AccountBankFeeServiceImpl implements AccountBankFeeService {

    @Autowired
    AccountbankFeeRepository repo;

    @Override
    public int insert(AccountBankFeeEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<AccountBankFeeEntity> getAccountBankFeesByBankId(String bankId) {
        return repo.getAccountBankFeesByBankId(bankId);
    }

    @Override
    public void updateStartDate(String date, String id) {
        repo.updateStartDate(date, id);
    }

    @Override
    public void udpateEndDate(String date, String id) {
        repo.updateEndDate(date, id);
    }

    @Override
    public List<AccountBankFeeInfoDTO> getAccountBankFeeByCustomerSyncId(String customerSyncId) {
        return repo.getAccountBankFeeByCustomerSyncId(customerSyncId);
    }

}
