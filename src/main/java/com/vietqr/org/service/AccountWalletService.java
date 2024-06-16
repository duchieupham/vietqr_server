package com.vietqr.org.service;

import com.vietqr.org.dto.IBalanceAndScoreDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountWalletEntity;

@Service
public interface AccountWalletService {

    public IBalanceAndScoreDTO getBalanceAndScore(String userId);

    public int insertAccountWallet(AccountWalletEntity entity);

    public AccountWalletEntity getAccountWalletByUserId(String userId);

    public void updatePointBySharingCode(long point, String sharingCode);

    public String checkExistedWalletId(String walletId);

    public String checkExistedSharingCode(String sharingCode);

    public void deleteAllAccountWallet();

    public String getUserIdByWalletId(String walletId);

    public void updateAmount(String amount, String id);
}
