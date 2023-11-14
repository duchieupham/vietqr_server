package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankReceiveServiceItemDTO;
import com.vietqr.org.dto.AnnualFeeBankDTO;
import com.vietqr.org.entity.AccountCustomerBankEntity;

@Repository
public interface AccountCustomerBankRepository extends JpaRepository<AccountCustomerBankEntity, Long> {

        @Query(value = "SELECT a.id as bankId, a.bank_account as bankAccount, b.bank_code as bankCode, b.bank_short_name as bankShortName "
                        + "FROM account_customer_bank f  "
                        + "INNER JOIN account_bank_receive a  "
                        + "ON f.bank_id = a.id  "
                        + "INNER JOIN bank_type b "
                        + "ON a.bank_type_id = b.id "
                        + "WHERE f.customer_sync_id = :customerSyncId ", nativeQuery = true)
        List<AnnualFeeBankDTO> getBanksAnnualFee(@Param(value = "customerSyncId") String customerSyncId);

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

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM account_customer_bank WHERE bank_id = :bankId AND customer_sync_id = :customerSyncId ", nativeQuery = true)
        void removeBankAccountFromCustomerSync(@Param(value = "bankId") String bankId,
                        @Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT bank_id FROM account_customer_bank WHERE customer_sync_id = :customerSyncId ", nativeQuery = true)
        List<String> getBankIdsByCustomerSyncId(@Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT a.bank_id as bankId, c.bank_account as bankAccount, c.bank_account_name as userBankName, d.bank_short_name as bankShortName, d.bank_code as bankCode, d.bank_name as bankName, d.img_id as imgId "
                        + "FROM account_customer_bank a  "
                        + "INNER JOIN customer_sync b  "
                        + "ON a.customer_sync_id = b.id  "
                        + "INNER JOIN account_bank_receive c  "
                        + "ON a.bank_id = c.id  "
                        + "INNER JOIN bank_type d  "
                        + "ON c.bank_type_id = d.id  "
                        + "WHERE a.customer_sync_id = :customerSyncId ", nativeQuery = true)
        List<AccountBankReceiveServiceItemDTO> getBankAccountsByMerchantId(
                        @Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT b.merchant "
                        + "FROM account_customer_bank a "
                        + "INNER JOIN customer_sync b "
                        + "ON a.customer_sync_id = b.id "
                        + "WHERE a.bank_id = :bankId "
                        + "LIMIT 1", nativeQuery = true)
        String getMerchantByBankId(@Param(value = "bankId") String bankId);

}
