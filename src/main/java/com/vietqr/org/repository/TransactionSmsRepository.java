package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vietqr.org.dto.TransactionSMSDetailDTO;
import com.vietqr.org.entity.TransactionSmsEntity;

public interface TransactionSmsRepository extends JpaRepository<TransactionSmsEntity, Long> {

        // get list
        // id, amount, trans_type, time, timePaid, content

        // all trans by smsID
        @Query(value = "SELECT * FROM transaction_sms "
                        + "WHERE sms_id = :smsId "
                        + "ORDER BY time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionSmsEntity> getTransactionSmsList(
                        @Param(value = "smsId") String smsId,
                        @Param(value = "offset") int offset);

        // all trans by status
        @Query(value = "SELECT * FROM transaction_sms "
                        + "WHERE sms_id = :smsId "
                        + "AND status = :status "
                        + "ORDER BY time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionSmsEntity> getTransactionSmsListByStatus(
                        @Param(value = "smsId") String smsId,
                        @Param(value = "status") int status,
                        @Param(value = "offset") int offset);

        // trans by bank
        @Query(value = "SELECT * FROM transaction_sms "
                        + "WHERE bank_id = :bankId "
                        + "ORDER BY time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionSmsEntity> getTransactionSmsListByBankId(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "offset") int offset);

        // trans by bank and status
        @Query(value = "SELECT * FROM transaction_sms "
                        + "WHERE bank_id = :bankId "
                        + "AND status = :status "
                        + "ORDER BY time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionSmsEntity> getTransactionSmsListByBankIdAndStatus(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "status") int status,
                        @Param(value = "offset") int offset);

        // get detail
        @Query(value = "SELECT * FROM transaction_sms "
                        + "WHERE id = :id ", nativeQuery = true)
        TransactionSmsEntity getTransactionSmsById(@Param(value = "id") String id);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.bank_id as bankId, "
                        + "a.content, a.address, a.amount, a.account_balance as accountBalance, "
                        + "a.time, a.time_paid as timePaid, a.trans_type as transType, a.reference_number as referenceNumber, "
                        + "a.sms_id as smsId, b.bank_account_name as userBankName, b.bank_type_id as bankTypeId, "
                        + "c.bank_short_name as bankShortName, c.bank_code as bankCode, c.bank_name as bankName, "
                        + "c.img_id as imgId "
                        + "FROM transaction_sms a "
                        + "INNER JOIN account_bank_sms b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.id = :id ", nativeQuery = true)
        TransactionSMSDetailDTO getTransactionSmsDetail(@Param(value = "id") String id);
}
