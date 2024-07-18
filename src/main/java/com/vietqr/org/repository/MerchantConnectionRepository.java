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

    @Query(value="SELECT COUNT(id) FROM merchant_connection", nativeQuery = true)
    Integer getCountingMerchantConnection();

    @Query(value = "SELECT id FROM merchant_connection WHERE mid = :mid ", nativeQuery = true)
    List<String> getIdMerchantConnectionByMid(String mid);

    @Query(value = "SELECT * FROM merchant_connection WHERE id = :id ", nativeQuery = true)
    MerchantConnectionEntity getMerchanConnectionById(String id);
}
