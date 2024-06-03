package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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

    @Query(value = "SELECT a.id AS invoiceItemId, a.name AS invoiceItemName, "
            + "a.unit AS unit, a.quantity AS quantity, a.amount AS amount, a.type AS type, "
            + "a.vat AS vat, a.vat_amount AS vatAmount, a.total_after_vat AS amountAfterVat, "
            + "a.total_amount AS totalAmount, a.status AS status, a.time_paid AS timePaid "
            + "FROM invoice_item a "
            + "WHERE a.invoice_id = :invoiceId ", nativeQuery = true)
    List<IInvoiceItemDetailDTO> getInvoiceItemsByInvoiceId(String invoiceId);

    @Query(value = "SELECT a.id AS itemId, COALESCE(a.total_amount, 0) AS totalAmount, "
            + "COALESCE(a.vat_amount, 0) AS vatAmount, "
            + "COALESCE(a.total_after_vat, 0) AS totalAmountAfterVat, "
            + "FROM invoice_item a "
            + "WHERE a.id = :itemId ", nativeQuery = true)
    IInvoiceItemRemoveDTO getInvoiceRemoveByInvoiceId(String itemId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM invoice_item WHERE id = :invoiceId", nativeQuery = true)
    void removeById(String invoiceId);

    @Query(value = "SELECT a.* "
            + "FROM invoice_item a "
            + "WHERE a.id = :itemId ", nativeQuery = true)
    InvoiceItemEntity getInvoiceItemById(String itemId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM invoice_item "
            + "WHERE invoice_id = :invoiceId "
            + "AND id NOT IN (:itemIds) ", nativeQuery = true)
    void removeByInvoiceId(String invoiceId, List<String> itemIds);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM invoice_item "
            + "WHERE invoice_id = :invoiceId ", nativeQuery = true)
    void removeByInvoiceId(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceItemId, a.name AS invoiceItemName, "
            + "a.unit AS unit, a.quantity AS quantity, a.type AS type, "
            + "a.amount AS amount, a.total_amount AS totalAmount, "
            + "a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_after_vat AS amountAfterVat, "
            + "a.status AS status, a.time_paid AS timePaid "
            + "FROM invoice_item a "
            + "WHERE a.id IN (:itemItemIds)", nativeQuery = true)
    List<IInvoiceItemDetailDTO> getInvoiceItemsByIds(List<String> itemItemIds);

    @Query(value = "SELECT COALESCE(SUM(CASE WHEN a.status = 1 THEN a.total_after_vat ELSE 0 END), 0) AS completeFee, "
            + "COALESCE(COUNT(CASE WHEN a.status = 1 && b.status = 1 THEN a.id ELSE NULL END), 0) AS completeCount, "
            + "COALESCE(SUM(CASE WHEN a.status = 0 THEN a.total_after_vat ELSE 0 END), 0) AS pendingFee, "
            + "COALESCE(COUNT(CASE WHEN a.status != 1 && b.status = 0 THEN a.id ELSE NULL END), 0) AS pendingCount, "
            + "COALESCE(COUNT(CASE WHEN a.status != 1 && b.status = 3 THEN a.id ELSE NULL END), 0) AS unfullyPaidCount "
            + "FROM invoice_item a "
            + "INNER JOIN invoice b ON a.invoice_id = b.id "
            + "WHERE b.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    IAdminExtraInvoiceDTO getExtraInvoice(long fromDate, long toDate);

    @Query(value = "SELECT a.merchant_id, a.bank_id "
            + "FROM invoice_item a "
            + "INNER JOIN invoice b ON a.invoice_id = b.id "
            + "WHERE a.process_date = :processDate ", nativeQuery = true)
    Boolean checkInvoiceItemExist(String processDate);

    List<InvoiceItemEntity> findInvoiceItemEntityByInvoiceId(String invoiceId);


}
