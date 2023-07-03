package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountWalletEntity;

@Repository
public interface AccountWalletRepository extends JpaRepository<AccountWalletEntity, Long> {

    @Query(value = "SELECT * FROM account_wallet WHERE user_id = :userId", nativeQuery = true)
    AccountWalletEntity getAccountWallet(@Param(value = "userId") String userId);

    @Query(value = "SELECT wallet_id FROM account_wallet WHERE wallet_id = :walletId", nativeQuery = true)
    String checkExistedWalletId(@Param(value = "walletId") String walletId);

    @Query(value = "SELECT sharing_code FROM account_wallet WHERE sharing_code = :sharingCode", nativeQuery = true)
    String checkExistedSharingCode(@Param(value = "sharingCode") String sharingCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_wallet SET point = point + :amount WHERE sharing_code = :sharingCode", nativeQuery = true)
    void updatePointBySharingCode(@Param(value = "amount") long amount,
            @Param(value = "sharingCode") String sharingCode);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_wallet", nativeQuery = true)
    void deleteAllAccountWallet();
}
