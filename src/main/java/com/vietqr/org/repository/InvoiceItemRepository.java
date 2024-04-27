package com.vietqr.org.repository;

import com.vietqr.org.dto.IInvoiceItemResponseDTO;
import com.vietqr.org.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, String> {
    @Query(value = "SELECT a.id AS invoiceItemId, a.name AS invoiceItemName, "
            + "a.quantity AS quantity, a.amount AS itemAmount, "
            + "a.total_amount AS totalItemAmount "
            + "FROM invoice_item a "
            + "WHERE a.invoice_id = :invoiceId", nativeQuery = true)
    List<IInvoiceItemResponseDTO> getInvoiceByInvoiceId(
            @Param(value = "invoiceId") String invoiceId);
}