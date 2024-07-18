package com.vietqr.org.service;

import com.vietqr.org.entity.BankReceiveConnectionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BankReceiveConnectionService {
    BankReceiveConnectionEntity getBankReceiveConnectionByBankIdAndMid(String id, String mid);

    void insertAll(List<BankReceiveConnectionEntity> bankReceiveConnectionEntities);

    List<BankReceiveConnectionEntity> getBankReceiveConnectionByBankId(String bankId);

    String checkBankAccountByBankIdAndMid(String bankId, String mid);
}
