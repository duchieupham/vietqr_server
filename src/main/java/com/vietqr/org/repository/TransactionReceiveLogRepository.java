package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveLogEntity;

@Repository
public interface TransactionReceiveLogRepository extends JpaRepository<TransactionReceiveLogEntity, Long> {

    @Query(value = "SELECT * FROM transaction_receive_log WHERE transaction_id = :transactionId ", nativeQuery = true)
    List<TransactionReceiveLogEntity> getTransactionReceiveLogs(@Param(value = "transactionId") String transactionId);
}
