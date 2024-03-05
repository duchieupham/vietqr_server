package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.bidv.CustomerItemInvoiceDataDTO;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;

@Repository
public interface CustomerItemInvoiceRepository extends JpaRepository<CustomerItemInvoiceEntity, Long> {

    @Query(value = "SELECT id, amount, bill_id as billId, "
            + "description, name, quantity, total_amount as totalAmount "
            + "FROM customer_item_invoice "
            + "WHERE bill_id = :billId ", nativeQuery = true)
    List<CustomerItemInvoiceDataDTO> getCustomerInvoiceItemByBillId(
            @Param(value = "billId") String billId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM customer_item_invoice WHERE bill_id = :billId", nativeQuery = true)
    void removeInvocieItemsByBillId(@Param(value = "billId") String billId);
}
