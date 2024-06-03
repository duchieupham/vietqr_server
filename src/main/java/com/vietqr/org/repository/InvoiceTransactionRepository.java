package com.vietqr.org.repository;

import com.vietqr.org.dto.InvoiceRequestPaymentDTO;
import com.vietqr.org.entity.InvoiceTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceTransactionRepository extends JpaRepository<InvoiceTransactionEntity, String> {
    @Query(value = "SELECT a.qr_code AS qrCode, a.id AS id, a.total_amount AS totalAmount, "
            + "b.name AS invoiceName, b.data AS data, b.invoice_id AS invoiceNumber, "
            + "a.amount AS amount, a.vat AS vat, a.vat_amount AS vatAmount, a.invoice_id AS invoiceId "
            + "FROM invoice_transaction a "
            + "INNER JOIN invoice b ON a.invoice_id = b.id "
            + "WHERE JSON_CONTAINS(a.invoice_item_ids, :itemIds) AND a.invoice_id = :invoiceId "
            + "AND JSON_LENGTH(a.invoice_item_ids) = JSON_LENGTH(:itemIds) "
            + "AND a.bank_id_recharge = :bankIdRecharge "
            + "LIMIT 1 ", nativeQuery = true)
    InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds, String bankIdRecharge);

    @Query(value = "SELECT a.qr_code AS qrCode, a.id AS id, a.total_amount AS totalAmount, "
            + "b.name AS invoiceName, b.data AS data, b.invoice_id AS invoiceNumber, "
            + "a.amount AS amount, a.vat AS vat, a.vat_amount AS vatAmount, a.invoice_id AS invoiceId "
            + "FROM invoice_transaction a "
            + "INNER JOIN invoice b ON a.invoice_id = b.id "
            + "WHERE JSON_CONTAINS(a.invoice_item_ids, :itemIds) AND a.invoice_id = :invoiceId "
            + "LIMIT 1 ", nativeQuery = true)
    InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds);

    Optional<InvoiceTransactionEntity> findByRefId(String refId);
}
