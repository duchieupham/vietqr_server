package com.vietqr.org.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
