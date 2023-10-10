package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
