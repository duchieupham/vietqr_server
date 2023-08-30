package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionRPAEntity;

@Repository
public interface TransactionRPARepository extends JpaRepository<TransactionRPAEntity, Long> {

}
