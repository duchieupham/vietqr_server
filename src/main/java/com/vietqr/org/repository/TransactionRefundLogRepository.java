package com.vietqr.org.repository;

import com.vietqr.org.entity.TransactionRefundLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRefundLogRepository extends JpaRepository<TransactionRefundLogEntity, String> {
    @Query(value = "SELECT * FROM transaction_refund_log WHERE reference_number = :referencenumber AND status = 1 LIMIT 1", nativeQuery = true)
    TransactionRefundLogEntity getByTransactionRefundByReferenceNumber(String referencenumber);
}
