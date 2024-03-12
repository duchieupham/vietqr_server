package com.vietqr.org.repository;

import com.vietqr.org.entity.MerchantConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantConnectionRepository extends JpaRepository<MerchantConnectionEntity, Long> {

    @Query(value = "SELECT b.id "
            + "FROM merchant b "
            + "INNER JOIN account_customer_merchant c "
            + "ON c.merchant_id = b.id "
            + "INNER JOIN account_customer d "
            + "ON d.id = c.account_customer_id "
            + "WHERE d.username  = :username ", nativeQuery = true)
    List<String> checkExistedCustomerSyncByUsername(String username);
}
