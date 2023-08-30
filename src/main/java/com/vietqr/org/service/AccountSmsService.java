package com.vietqr.org.service;

import org.jvnet.hk2.annotations.Service;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.dto.AccountSmsSearchDTO;
import com.vietqr.org.entity.AccountSmsEntity;

@Service
public interface AccountSmsService {

    public int insert(AccountSmsEntity entity);

    public String loginSms(String phoneNo, String password);

    public AccountCheckDTO checkExistedPhoneNo(String phoneNo);

    public AccountSmsSearchDTO getAccountSmsSearch(String phoneNo);

    public AccountSmsEntity getAccountSmsById(String id);

    public void updatePassword(String password, String id);

    public void updateStatus(int status, String id);

    public void updateAccessLoginSms(long lastLogin, long accessCount, String id);

    public Long getAccessCountById(String id);
}
