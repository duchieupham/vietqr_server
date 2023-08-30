package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.dto.AccountSmsSearchDTO;
import com.vietqr.org.entity.AccountSmsEntity;
import com.vietqr.org.repository.AccountSMSRepository;

@Service
public class AccountSmsServiceImpl implements AccountSmsService {
    @Autowired
    AccountSMSRepository repo;

    @Override
    public int insert(AccountSmsEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String loginSms(String phoneNo, String password) {
        return repo.loginSms(phoneNo, password);
    }

    @Override
    public AccountCheckDTO checkExistedPhoneNo(String phoneNo) {
        return repo.checkExistedPhoneNo(phoneNo);
    }

    @Override
    public AccountSmsSearchDTO getAccountSmsSearch(String phoneNo) {
        return repo.getAccountSmsSearch(phoneNo);
    }

    @Override
    public AccountSmsEntity getAccountSmsById(String id) {
        return repo.getAccountSmsById(id);
    }

    @Override
    public void updatePassword(String password, String id) {
        repo.updatePassword(password, id);
    }

    @Override
    public void updateStatus(int status, String id) {
        repo.updateStatus(status, id);
    }

    @Override
    public void updateAccessLoginSms(long lastLogin, long accessCount, String id) {
        repo.updateAccessLoginSms(lastLogin, accessCount, id);
    }

    @Override
    public Long getAccessCountById(String id) {
        return repo.getAccessCountById(id);
    }

}
