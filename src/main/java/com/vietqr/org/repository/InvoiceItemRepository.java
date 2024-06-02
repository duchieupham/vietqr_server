package com.vietqr.org.repository;

import com.vietqr.org.dto.IInvoiceItemDetailDTO;
import com.vietqr.org.dto.IInvoiceItemRemoveDTO;
import com.vietqr.org.dto.IInvoiceItemResponseDTO;
import com.vietqr.org.dto.InvoiceUpdateItemDTO;
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
}
