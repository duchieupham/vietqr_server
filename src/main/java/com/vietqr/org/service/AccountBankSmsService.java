package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankSmsDTO;
import com.vietqr.org.dto.AccountBankSmsDetailDTO;
import com.vietqr.org.entity.AccountBankSmsEntity;

@Service
public interface AccountBankSmsService {

    public int insert(AccountBankSmsEntity entity);

    public int insertAll(List<AccountBankSmsEntity> entities);

    public List<AccountBankSmsDTO> getListBankAccountSmsBySmsId(String smsId);

    public AccountBankSmsDTO getBankAccountSmsById(String id);

    public AccountBankSmsDetailDTO getAccountBankSmsDetail(String id);
}
