package com.vietqr.org.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Repository
public interface TransactionReceiveBranchRepository extends JpaRepository<TransactionReceiveBranchEntity, Long> {

        // get related 5 transactions by businessId

        // get list transaction paging by branchId

        // find transaction-branch by transaction id
        @Query(value = "SELECT * "
                        + "FROM transaction_receive_branch "
                        + "WHERE transaction_receive_id = :transactionId", nativeQuery = true)
        TransactionReceiveBranchEntity getTransactionBranchByTransactionId(
                        @Param(value = "transactionId") String transactionId);

        @Query(value = "SELECT a.id as transactionId, a.amount, a.bank_account as bankAccount, a.content, a.time, a.status, a.type "
                        + "FROM transaction_receive a "
                        + "INNER JOIN transaction_receive_branch b "
                        + "ON a.id = b.transaction_receive_id "
                        + "WHERE b.branch_id = :branchId "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByBranchId(@Param(value = "branchId") String branchId,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT a.id as transactionId, a.amount, a.bank_account as bankAccount, a.content, a.time, a.status, a.type "
                        + "FROM transaction_receive a "
                        + "INNER JOIN transaction_receive_branch b "
                        + "ON a.id = b.transaction_receive_id "
                        + "WHERE b.business_id = :businessId "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByBusinessId(@Param(value = "businessId") String businessId,
                        @Param(value = "offset") int offset);
}
