package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionRPAEntity;

@Repository
public interface TransactionRPARepository extends JpaRepository<TransactionRPAEntity, Long> {

    @Query(value = "SELECT id FROM transactionrpa WHERE reference_number = :referenceNumber ", nativeQuery = true)
    List<String> checkExistedTransaction(@Param(value = "referenceNumber") String referenceNumber);
}
