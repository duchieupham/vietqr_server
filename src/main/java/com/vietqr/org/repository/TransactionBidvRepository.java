package com.vietqr.org.repository;

import com.vietqr.org.entity.TransactionBidvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionBidvRepository extends JpaRepository<TransactionBidvEntity, String> {
}
