package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankRequestItemDTO;
import com.vietqr.org.entity.AccountBankRequestEntity;

@Service
public interface AccountBankRequestService {

    public int insert(AccountBankRequestEntity entity);

    public List<AccountBankRequestItemDTO> getAccountBankRequests(int offset);

    public void updateAccountBankRequest(
            String bankAccount,
            String userBankName,
            String bankCode,
            String nationalId,
            String phoneAuthenticated,
            int requestType,
            String address,
            String id);

    public void deleteAccountBankRequest(String id);
}
