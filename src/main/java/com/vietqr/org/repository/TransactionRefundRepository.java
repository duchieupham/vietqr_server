package com.vietqr.org.repository;

import com.vietqr.org.dto.IRefundCheckOrderDTO;
import com.vietqr.org.dto.TransactionCheckMultiTimesDTO;
import com.vietqr.org.entity.TransactionRefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRefundRepository extends JpaRepository<TransactionRefundEntity, String> {
    @Query(value = "SELECT COALESCE(COUNT(id), 0) AS refundCount, "
            + "COALESCE(SUM(amount), 0) AS amountRefunded, "
            + "transaction_id AS transactionId "
            + "FROM transaction_refund "
            + "WHERE transaction_id IN (:transactionIds) AND status = 1 "
            + "GROUP BY transaction_id ", nativeQuery = true)
    List<IRefundCheckOrderDTO> getTotalRefundedByTransactionId(List<String> transactionIds);

    @Query(value = "SELECT bank_account AS bankAccount, "
            + "multi_times AS multiTimes, status AS status, "
            + "ref_number AS refNumber, reference_number AS referenceNumber "
            + "FROM transaction_refund "
            + "WHERE bank_account = :bankAccount "
            + "AND status = 1 AND ref_number = :referenceNumber "
            + "ORDER BY time ASC LIMIT 1", nativeQuery = true)
    TransactionCheckMultiTimesDTO getTransactionRefundCheck(String bankAccount, String referenceNumber);

    @Query(value = "SELECT ref_number "
            + "FROM transaction_refund "
            + "WHERE bank_account = :bankAccount "
            + "AND status = 1 AND reference_number = :referenceNumber "
            + "LIMIT 1", nativeQuery = true)
    String checkExistRefundTransaction(String bankAccount, String referenceNumber);
}
