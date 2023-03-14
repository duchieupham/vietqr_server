package com.vietqr.org.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Repository
public interface TransactionReceiveBranchRepository extends JpaRepository<TransactionReceiveBranchEntity, Long> {

    // get related 5 transactions by businessId

    // get list transaction paging by branchId
}
