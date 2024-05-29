package com.vietqr.org.repository;

import com.vietqr.org.dto.AccountCustomerMerchantDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountCustomerEntity;

import java.util.List;

@Repository
public interface AccountCustomerRepository extends JpaRepository<AccountCustomerEntity, Long> {

    @Query(value = "SELECT * FROM account_customer WHERE username = :username AND available = true", nativeQuery = true)
    AccountCustomerEntity getUserByUsername(@Param(value = "username") String username);

    @Query(value = "SELECT username FROM account_customer WHERE password = :password ", nativeQuery = true)
    String getAccessKey(@Param(value = "password") String password);

    @Query(value = "SELECT password FROM account_customer WHERE username = :username ", nativeQuery = true)
    String getAccessKeyByUsername(String username);

    @Query(value = "SELECT merchant FROM account_customer a"
            + " INNER JOIN account_customer_bank b ON a.id = b.account_customer_id"
            + " LEFT JOIN customer_sync c ON b.customer_sync_id = c.id"
            + " WHERE a.password = :pw ", nativeQuery = true)
    List<AccountCustomerMerchantDTO> getMerchantNameByPw(String pw);

    @Query(value = "SELECT id FROM account_customer"
            + " WHERE username = :username ", nativeQuery = true)
    String getAccountCustomerIdByUsername(String username);


}