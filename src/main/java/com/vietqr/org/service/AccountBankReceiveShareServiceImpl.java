package com.vietqr.org.service;

import com.vietqr.org.dto.AccountBankReceiveShareDTO;
import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import com.vietqr.org.repository.AccountBankReceiveShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountBankReceiveShareServiceImpl implements AccountBankReceiveShareService {

    @Autowired
    private AccountBankReceiveShareRepository repo;

    @Override
    public int insertAccountBankReceiveShare(AccountBankReceiveShareEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<AccountBankReceiveShareDTO> getAccountBankReceiveShares(String userId) {
        return repo.getAccountBankReceiveShare(userId);
    }

    @Override
    public List<AccountMemberDTO> getMembersFromBankReceiveShare(String bankId) {
        return repo.getMembersFromBank(bankId);
    }

    @Override
    public void removeMemberFromBankReceiveShare(String userId, String bankId) {
        repo.removeMemberFromBank(userId, bankId);
    }

    @Override
    public void removeAllMemberFromBankReceiveShare(String bankId) {
        repo.removeAllMemberFromBank(bankId);
    }

    @Override
    public String checkUserExistedFromBankReceiveShare(String bankId, String id) {
        return repo.checkUserExistedFromBank(bankId, id);
    }

    @Override
    public void deleteAccountBankReceiveShareByBankId(String bankId) {
        repo.deleteAccountBankReceiveShareByBankId(bankId);
    }
}
