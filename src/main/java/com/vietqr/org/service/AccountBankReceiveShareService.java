package com.vietqr.org.service;

import com.vietqr.org.dto.AccountBankReceiveShareDTO;
import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountBankReceiveShareService {

    public int insertAccountBankReceiveShare(AccountBankReceiveShareEntity entity);

    public List<AccountBankReceiveShareDTO> getAccountBankReceiveShares(String userId);

    public List<AccountMemberDTO> getMembersFromBankReceiveShare(String bankId);

    public void removeMemberFromBankReceiveShare(String userId, String bankId);

    public void removeAllMemberFromBankReceiveShare(String bankId);

    public String checkUserExistedFromBankReceiveShare(String bankId, String id);

    public void deleteAccountBankReceiveShareByBankId(String bankId);
}
