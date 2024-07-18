package com.vietqr.org.repository;

import com.vietqr.org.entity.ProcessedInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedInvoiceRepository extends JpaRepository<ProcessedInvoiceEntity, String> {
}
