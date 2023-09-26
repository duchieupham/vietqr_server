package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankFeeEntity;

@Service
public interface AccountBankFeeService {

    public int insert(AccountBankFeeEntity entity);

    public List<AccountBankFeeEntity> getAccountBankFeesByBankId(String bankId);

    public void updateStartDate(String date, String id);

    public void udpateEndDate(String date, String id);
}
