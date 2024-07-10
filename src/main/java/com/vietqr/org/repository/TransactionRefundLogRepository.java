package com.vietqr.org.repository;

import com.vietqr.org.entity.TransactionRefundLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRefundLogRepository extends JpaRepository<TransactionRefundLogEntity, String> {
}
