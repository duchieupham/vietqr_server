package com.vietqr.org.repository;

import java.util.List;

import com.vietqr.org.dto.ITransactionReceiveLogDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveLogEntity;

@Repository
public interface TransactionReceiveLogRepository extends JpaRepository<TransactionReceiveLogEntity, Long> {

    @Query(value = "SELECT * FROM transaction_receive_log WHERE transaction_id = :transactionId ", nativeQuery = true)
    List<TransactionReceiveLogEntity> getTransactionReceiveLogs(@Param(value = "transactionId") String transactionId);

    @Query(value = "SELECT id AS id, type AS type, status AS status, "
            + "time AS timeRequest, time_response AS timeResponse, "
            + "status_code AS statusCode, message AS message, transaction_id AS transactionId "
            + "FROM transaction_receive_log WHERE transaction_id = :transactionId ", nativeQuery = true)
    List<ITransactionReceiveLogDTO> getTransactionLogsByTransId(@Param(value = "transactionId") String transactionId);
}
