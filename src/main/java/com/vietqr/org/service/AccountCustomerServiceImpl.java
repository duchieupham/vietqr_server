package com.vietqr.org.service;

import com.vietqr.org.dto.AccountCustomerMerchantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerEntity;
import com.vietqr.org.repository.AccountCustomerRepository;

import java.util.List;

@Service
public class AccountCustomerServiceImpl implements AccountCustomerService {

    @Autowired
    AccountCustomerRepository repo;

    @Override
    public int insert(AccountCustomerEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String getAccessKey(String password) {
        return repo.getAccessKey(password);
    }

    @Override
    public String getAccessKeyByUsername(String username) {
        return repo.getAccessKeyByUsername(username);
    }

    @Override
    public List<AccountCustomerMerchantDTO> getMerchantNameByPassword(String pw) {
        return repo.getMerchantNameByPw(pw);
    }

    @Override
    public String getAccountCustomerIdByUsername(String username) {
        return repo.getAccountCustomerIdByUsername(username);
    }

}
