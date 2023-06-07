package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CustomerSyncEntity;

@Repository
public interface CustomerSyncRepository extends JpaRepository<CustomerSyncEntity, Long> {

    @Query(value = "SELECT * FROM customer_sync WHERE active = true", nativeQuery = true)
    List<CustomerSyncEntity> getCustomerSyncEntities();

    @Query(value = "SELECT * FROM customer_sync WHERE id = :id", nativeQuery = true)
    CustomerSyncEntity getCustomerSyncById(@Param(value = "id") String id);

    @Query(value = "SELECT user_id FROM customer_sync WHERE user_id = :userId", nativeQuery = true)
    String checkExistedCustomerSync(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE customer_sync SET information = :information WHERE user_id = :userId", nativeQuery = true)
    void updateCustomerSyncInformation(@Param(value = "information") String information,
            @Param(value = "userId") String userId);

}