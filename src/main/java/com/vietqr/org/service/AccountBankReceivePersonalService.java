package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;;

@Service
public interface AccountBankReceivePersonalService {

    public int insertAccountBankReceivePersonal(BankReceivePersonalEntity entity);

    public List<AccountBankReceivePersonalDTO> getBankReceivePersonals(String userId);

    public void deleteBankReceivePersonalByBankId(String bankId);
}
