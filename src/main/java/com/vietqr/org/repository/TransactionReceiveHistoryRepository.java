package com.vietqr.org.repository;

import com.vietqr.org.entity.TransactionReceiveHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionReceiveHistoryRepository extends JpaRepository<TransactionReceiveHistoryEntity, String> {
}
