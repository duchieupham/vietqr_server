package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.dto.bidv.CustomerVaItemDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Repository
public interface CustomerVaRepository extends JpaRepository<CustomerVaEntity, Long> {

        @Query(value = "SELECT COUNT(id) FROM customer_va", nativeQuery = true)
        Long getCustomerVaLength();

        @Query(value = "SELECT * FROM customer_va WHERE id = :id LIMIT 1", nativeQuery = true)
        CustomerVaEntity getCustomerVaInfoById(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM customer_va WHERE user_id = :userId "
                        + "AND merchant_id = :merchantId ", nativeQuery = true)
        void removeCustomerVa(
                        @Param(value = "userId") String userId,
                        @Param(value = "merchantId") String merchantId);

        @Query(value = "SELECT customer_id, user_bank_name as customer_name FROM customer_va "
                        + "WHERE customer_id = :customerId ", nativeQuery = true)
        CustomerVaInfoDataDTO getCustomerVaInfo(@Param(value = "customerId") String customerId);

        @Query(value = "SELECT customer_id "
                        + "FROM customer_va "
                        + "WHERE customer_id = :customerId "
                        + "LIMIT 1", nativeQuery = true)
        String checkExistedCustomerId(@Param(value = "customerId") String customerId);

        @Query(value = "SELECT id "
                        + "FROM customer_va "
                        + "WHERE bank_account = :bankAccount "
                        + "LIMIT 1", nativeQuery = true)
        String checkExistedLinkedBankAccount(@Param(value = "bankAccount") String bankAccount);

        //
        @Query(value = "SELECT a.id, a.merchant_name as merchantName, a.merchant_id as merchantId, a.customer_id as customerId, a.bank_account as bankAccount, "
                        + "COALESCE(b.amount, 0) as unpaidInvoiceAmount "
                        + "FROM customer_va a "
                        + "LEFT JOIN ( "
                        + "SELECT customer_id, amount "
                        + "FROM customer_invoice "
                        + "WHERE status = 0 "
                        + ") b "
                        + "ON a.customer_id = b.customer_id "
                        + "WHERE a.user_id = :userId ", nativeQuery = true)
        List<CustomerVaItemDTO> getCustomerVasByUserId(
                        @Param(value = "userId") String userId);

        @Query(value = "SELECT user_id FROM customer_va WHERE customer_id = :customerId LIMIT 1", nativeQuery = true)
        String getUserIdByCustomerId(@Param(value = "customerId") String customerId);
}
