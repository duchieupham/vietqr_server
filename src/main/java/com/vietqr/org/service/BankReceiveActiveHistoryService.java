package com.vietqr.org.service;

import com.vietqr.org.dto.BankActiveAdminDataDTO;
import com.vietqr.org.dto.ICheckKeyActiveDTO;
import com.vietqr.org.entity.BankReceiveActiveHistoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BankReceiveActiveHistoryService {
    void insert(BankReceiveActiveHistoryEntity bankReceiveActiveHistoryEntity);

    BankActiveAdminDataDTO getBankActiveAdminData(String keyActive);

    BankReceiveActiveHistoryEntity getBankReceiveActiveByUserIdAndBankId(String userId, String bankId);

    List<ICheckKeyActiveDTO> getBankReceiveActiveByUserIdAndBankIdBackUp(String userId, String bankId);
}
