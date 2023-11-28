package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CustomerSyncMappingEntity;

@Repository
public interface CustomerSyncMappingRepository extends JpaRepository<CustomerSyncMappingEntity, Long> {

    @Query(value = "SELECT * FROM customer_sync_mapping WHERE user_id = :userId", nativeQuery = true)
    List<CustomerSyncMappingEntity> getCustomerSyncMappingByUserId(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE customer_sync_mapping SET cus_sync_id = :customerSyncId "
            + "WHERE user_id = :userId ", nativeQuery = true)
    void updateCustomerSyncMapping(
            @Param(value = "customerSyncId") String customerSyncId,
            @Param(value = "userId") String userId);
}
