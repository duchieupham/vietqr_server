package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveImageEntity;

@Repository
public interface TransactionReceiveImageRepository extends JpaRepository<TransactionReceiveImageEntity, Long> {

    @Query(value = "SELECT img_id FROM transaction_receive_image WHERE transaction_receive_id = :transactionReceiveId", nativeQuery = true)
    List<String> getImgIdsByTransReceiveId(@Param(value = "transactionReceiveId") String transactionReceiveId);
}
