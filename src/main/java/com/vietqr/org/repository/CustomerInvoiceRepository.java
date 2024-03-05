package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoiceEntity, Long> {

    @Query(value = "SELECT bill_id FROM customer_invoice WHERE bill_id = :billId LIMIT 1", nativeQuery = true)
    String checkExistedBillId(@Param(value = "billId") String billId);

    @Query(value = "SELECT bill_id as billId, amount, status, type, name, "
            + "time_created as timeCreated, time_paid as timePaid "
            + "FROM customer_invoice "
            + "WHERE customer_id = :customerId "
            + "ORDER BY time_created DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<CustomerInvoiceDataDTO> getCustomerInvoiceAllStatus(
            @Param(value = "customerId") String customerId,
            @Param(value = "offset") int offset);

    @Query(value = "SELECT bill_id as billId, amount, status, type, name, "
            + "time_created as timeCreated, time_paid as timePaid "
            + "FROM customer_invoice "
            + "WHERE bill_id = :billId ", nativeQuery = true)
    CustomerInvoiceDataDTO getCustomerInvoiceByBillId(
            @Param(value = "billId") String billId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM customer_invoice WHERE bill_id = :billId", nativeQuery = true)
    void removeInvocieByBillId(@Param(value = "billId") String billId);
}
