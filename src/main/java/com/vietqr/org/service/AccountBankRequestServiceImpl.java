package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankRequestItemDTO;
import com.vietqr.org.entity.AccountBankRequestEntity;
import com.vietqr.org.repository.AccountBankRequestRepository;

@Service
public class AccountBankRequestServiceImpl implements AccountBankRequestService {

    @Autowired
    AccountBankRequestRepository repo;

    @Override
    public int insert(AccountBankRequestEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<AccountBankRequestItemDTO> getAccountBankRequests(int offset) {
        return repo.getAccountBankRequests(offset);
    }

    @Override
    public void updateAccountBankRequest(String bankAccount, String userBankName, String bankCode, String nationalId,
            String phoneAuthenticated, int requestType, String address, String id) {
        repo.updateAccountBankRequest(bankAccount, userBankName, bankCode, nationalId, phoneAuthenticated, requestType,
                address, id);
    }

    @Override
    public void deleteAccountBankRequest(String id) {
        repo.deleteAccountBankRequest(id);
    }

}
