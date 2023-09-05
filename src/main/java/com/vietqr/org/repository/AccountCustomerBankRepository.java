package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountCustomerBankEntity;

@Repository
public interface AccountCustomerBankRepository extends JpaRepository<AccountCustomerBankEntity, Long> {

        @Query(value = "SELECT * FROM account_customer_bank WHERE bank_id = :bankId", nativeQuery = true)
        List<AccountCustomerBankEntity> getAccountCustomerBankByBankId(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT id FROM account_customer_bank WHERE bank_id = :bankId AND customer_sync_id = :customerSyncId", nativeQuery = true)
        String checkExistedAccountCustomerBank(@Param(value = "bankId") String bankId,
                        @Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT id FROM account_customer_bank WHERE bank_account = :bankAccount AND customer_sync_id = :customerSyncId", nativeQuery = true)
        List<String> checkExistedAccountCustomerBankByBankAccount(@Param(value = "bankAccount") String bankAccount,
                        @Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT c.id "
                        + "FROM account_customer_bank a "
                        + "INNER JOIN account_customer b "
                        + "ON a.account_customer_id = b.id "
                        + "INNER JOIN customer_sync c "
                        + "ON a.customer_sync_id = c.id "
                        + "WHERE b.username = :username ", nativeQuery = true)
        List<String> checkExistedCustomerSyncByUsername(@Param(value = "username") String username);

}
