package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Repository
public interface TransactionReceiveRepository extends JpaRepository<TransactionReceiveEntity, Long> {

        @Query(value = "SELECT * FROM transaction_receive WHERE id = :id", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveById(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_receive SET status = :status, ref_id = :refId, reference_number = :referenceNumber WHERE id = :id", nativeQuery = true)
        void updateTransactionReceiveStatus(@Param(value = "status") int status, @Param(value = "refId") String refId,
                        @Param(value = "referenceNumber") String referenceNumber, @Param(value = "id") String id);

        @Query(value = "SELECT b.id as transactionId, b.amount, b.bank_account as bankAccount, b.content, b.time, b.status, b.type, b.trans_type as transType "
                        + "FROM transaction_receive_branch a "
                        + "INNER JOIN transaction_receive b "
                        + "ON a.transaction_receive_id = b.id "
                        + "WHERE a.business_id = :businessId "
                        + "ORDER BY b.time DESC LIMIT 5", nativeQuery = true)
        List<TransactionRelatedDTO> getRelatedTransactionReceives(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id=:bankId "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                        @Param(value = "bankId") String bankId);

        @Query(value = "SELECT a.id, a.amount, a.bank_id as bankId, b.bank_account as bankAccount, a.content, a.ref_id as refId, a.status, a.time, a.type, a.trace_id as traceId, a.trans_type as transType, b.bank_account_name as bankAccountName, c.bank_code as bankCode, c.bank_name as bankName, c.img_id as imgId, a.reference_number as referenceNumber "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + " WHERE a.id = :id", nativeQuery = true)
        TransactionDetailDTO getTransactionById(@Param(value = "id") String id);

        @Query(value = "SELECT tr.* "
                        + "FROM transaction_receive tr "
                        + "JOIN account_bank_receive abr ON tr.bank_id = abr.id "
                        + "JOIN bank_type bt ON abr.bank_type_id = bt.id "
                        + "WHERE tr.trace_id = :id "
                        + "AND tr.amount = :amount "
                        + "AND tr.status = 0 "
                        + "AND bt.status = 1", nativeQuery = true)
        TransactionReceiveEntity getTransactionByTraceId(@Param(value = "id") String id,
                        @Param(value = "amount") Long amount);

        @Query(value = "SELECT * "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId "
                        + "ORDER BY time DESC LIMIT 5", nativeQuery = true)
        List<TransactionReceiveEntity> getRelatedTransactionByBankId(@Param(value = "bankId") String bankId);

}
