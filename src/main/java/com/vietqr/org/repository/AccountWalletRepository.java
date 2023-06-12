package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountWalletEntity;

@Repository
public interface AccountWalletRepository extends JpaRepository<AccountWalletEntity, Long> {

    @Query(value = "SELECT * FROM account_wallet WHERE user_id = :userId", nativeQuery = true)
    AccountWalletEntity getAccountWallet(@Param(value = "userId") String userId);
}
