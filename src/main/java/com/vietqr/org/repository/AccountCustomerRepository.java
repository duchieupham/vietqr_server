package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountCustomerEntity;

@Repository
public interface AccountCustomerRepository extends JpaRepository<AccountCustomerEntity, Long> {

    @Query(value = "SELECT * FROM account_customer WHERE username = :username AND available = true", nativeQuery = true)
    AccountCustomerEntity getUserByUsername(@Param(value = "username") String username);

    @Query(value = "SELECT username FROM account_customer WHERE password = :password ", nativeQuery = true)
    String getAccessKey(@Param(value = "password") String password);

    @Query(value = "SELECT password FROM account_customer WHERE username = :username ", nativeQuery = true)
    String getAccessKeyByUsername(String username);

    @Query(value = "SELECT b.id FROM account_customer a "
            + "INNER JOIN merchant_sync b ON b.account_customer_id = a.id "
            + "WHERE a.username = :username ", nativeQuery = true)
    String checkExistMerchantSyncByUsername(String username);

    @Query(value = "SELECT b.publish_id FROM account_customer a "
            + "INNER JOIN merchant_sync b ON b.account_customer_id = a.id "
            + "WHERE a.username = :username ", nativeQuery = true)
    String checkExistMerchantSyncByUsernameV2(String username);
}