package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankFeeInfoDTO;
import com.vietqr.org.entity.AccountBankFeeEntity;

@Repository
public interface AccountbankFeeRepository extends JpaRepository<AccountBankFeeEntity, Long> {

        @Query(value = "SELECT * FROM account_bank_fee WHERE bank_id = :bankId ", nativeQuery = true)
        List<AccountBankFeeEntity> getAccountBankFeesByBankId(@Param(value = "bankId") String bankId);

        // update start Date - end Date
        @Transactional
        @Modifying
        @Query(value = "UPDATE account_bank_fee SET start_date = :startDate WHERE id = :id", nativeQuery = true)
        void updateStartDate(@Param(value = "startDate") String startDate, @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE account_bank_fee SET end_date = :endDate WHERE id = :id", nativeQuery = true)
        void updateEndDate(@Param(value = "endDate") String endDate, @Param(value = "id") String id);

        @Query(value = "SELECT b.id as bankId, a.id as accBankFeeId, b.bank_account as bankAccount, c.bank_short_name as bankShortName, c.bank_code as bankCode, "
                        + "a.annual_fee as annualFee, a.counting_trans_type as countingTransType, a.percent_fee as percentFee, "
                        + "a.short_name as shortName, a.trans_fee as transFee, a.vat "
                        + "FROM account_bank_fee a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id "
                        + "WHERE d.customer_sync_id = :customerSyncId ", nativeQuery = true)
        List<AccountBankFeeInfoDTO> getAccountBankFeeByCustomerSyncId(
                        @Param(value = "customerSyncId") String customerSyncId);
}
