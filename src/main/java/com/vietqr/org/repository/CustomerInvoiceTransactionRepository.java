package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.bidv.CustomerInvoiceTransactionEntity;

@Repository
public interface CustomerInvoiceTransactionRepository extends JpaRepository<CustomerInvoiceTransactionEntity, Long> {

}
