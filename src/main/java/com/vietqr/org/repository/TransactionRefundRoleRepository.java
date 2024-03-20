package com.vietqr.org.repository;

import com.vietqr.org.entity.TransactionRefundRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRefundRoleRepository extends JpaRepository<TransactionRefundRoleEntity, String> {
}
