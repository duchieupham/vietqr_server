package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoiceEntity, Long> {

        @Query(value = "SELECT bill_id FROM customer_invoice WHERE bill_id = :billId LIMIT 1", nativeQuery = true)
        String checkExistedBillId(@Param(value = "billId") String billId);

        @Query(value = "SELECT a.bill_id as billId, a.amount, a.status, a.type, a.name, "
                        + "a.time_created as timeCreated, a.time_paid as timePaid, "
                        + "b.user_bank_name as userBankName, b.bank_account as bankAccount, b.customer_id as customerId "
                        + "FROM customer_invoice a "
                        + "INNER JOIN customer_va b "
                        + "ON a.customer_id = b.customer_id "
                        + "WHERE a.customer_id = :customerId "
                        + "ORDER BY a.time_created DESC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<CustomerInvoiceDataDTO> getCustomerInvoiceAllStatus(
                        @Param(value = "customerId") String customerId,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT a.bill_id as billId, a.amount, a.status, a.type, a.name, "
                        + "a.time_created as timeCreated, a.time_paid as timePaid, "
                        + "b.user_bank_name as userBankName, b.bank_account as bankAccount, b.customer_id as customerId "
                        + "FROM customer_invoice a "
                        + "INNER JOIN customer_va b "
                        + "ON a.customer_id = b.customer_id "
                        + "WHERE a.bill_id = :billId ", nativeQuery = true)
        CustomerInvoiceDataDTO getCustomerInvoiceByBillId(
                        @Param(value = "billId") String billId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM customer_invoice WHERE bill_id = :billId", nativeQuery = true)
        void removeInvocieByBillId(@Param(value = "billId") String billId);

        @Query(value = "SELECT type, amount, bill_id "
                        + "FROM customer_invoice "
                        + "WHERE customer_id = :customerId "
                        + "AND status = 0 "
                        + "ORDER BY RAND() "
                        + "LIMIT 1", nativeQuery = true)
        CustomerInvoiceInfoDataDTO getCustomerInvoiceInfo(@Param(value = "customerId") String customerId);


        // bỏ inquire = 0
        @Query(value = "SELECT type, amount, bill_id "
                + "FROM customer_invoice "
                + "WHERE customer_id = :customerId "
                + "AND status = 0 "
                + "ORDER BY RAND() ", nativeQuery = true)
        List<CustomerInvoiceInfoDataDTO> getCustomerInvoiceInfos(@Param(value = "customerId") String customerId);

        @Transactional
        @Modifying
        @Query(value = "UPDATE customer_invoice "
                        + "SET inquire = :inquired "
                        + "WHERE bill_id = :billId", nativeQuery = true)
        void updateInquiredInvoiceByBillId(
                        @Param(value = "inquired") int inquired,
                        @Param(value = "billId") String billId);

        @Transactional
        @Modifying
        @Query(value = "UPDATE customer_invoice "
                        + "SET status = :status, "
                        + "time_paid = :timePaid "
                        + "WHERE bill_id = :billId ", nativeQuery = true)
        void updateCustomerVaInvoice(
                        @Param(value = "status") int status,
                        @Param(value = "timePaid") Long timePaid,
                        @Param(value = "billId") String billId);

        @Query(value = "SELECT customer_id FROM customer_invoice WHERE bill_id = :billId ", nativeQuery = true)
        String getCustomerIdByBillId(@Param(value = "billId") String billId);
}
