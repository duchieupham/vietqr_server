package com.vietqr.org.repository;

import com.vietqr.org.dto.IInvoiceDetailDTO;
import com.vietqr.org.dto.IInvoiceResponseDTO;
import com.vietqr.org.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "b.content AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, int status, int offset, int size);

    @Query(value = "SELECT invoice_id FROM invoice "
            + "WHERE invoice_id = :invoiceId", nativeQuery = true)
    String checkDuplicatedInvoiceId(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "b.content AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, int status, long fromDate,
                                                         long toDate, int offset, int size);

    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "b.content AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.bank_id = :bankId "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, int status,
                                                          String bankId, int offset, int size);

    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "b.content AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, int status, String bankId,
                                                                  long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status ", nativeQuery = true)
    int countInvoiceByUserId(String userId, int status);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByUserIdAndMonth(String userId, int status,
                                     long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.bank_id = :bankId ", nativeQuery = true)
    int countInvoiceByUserIdAndBankId(String userId, int status, String bankId);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.user_id = :userId AND a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId ", nativeQuery = true)
    int countInvoiceByUserIdAndBankIdAndMonth(String userId, int status,
                                              String bankId, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "a.invoice_id AS invoiceNumber, a.name AS invoiceName, "
            + "a.time_created AS timeCreated, a.time_paid AS timePaid, "
            + "a.status AS status, a.vat_amount AS vatAmount, "
            + "a.vat AS vat, a.bank_id AS bankId, b.content AS content, "
            + "a.amount AS amount, a.total_amount AS totalAmount, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.id = :invoiceId ", nativeQuery = true)
    IInvoiceDetailDTO getInvoiceDetailById(String invoiceId);
}
