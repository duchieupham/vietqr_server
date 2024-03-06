package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Repository
public interface CustomerVaRepository extends JpaRepository<CustomerVaEntity, Long> {

        @Query(value = "SELECT COUNT(id) FROM customer_va", nativeQuery = true)
        Long getCustomerVaLength();

        @Query(value = "SELECT * FROM customer_va WHERE bank_id = :bankId LIMIT 1", nativeQuery = true)
        CustomerVaEntity getCustomerVaInfoByBankId(@Param(value = "bankId") String bankId);

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
}
