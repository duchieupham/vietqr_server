package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CustomerSyncEntity;

@Repository
public interface CustomerSyncRepository extends JpaRepository<CustomerSyncEntity, Long> {

    @Query(value = "SELECT * FROM customer_sync WHERE active = true", nativeQuery = true)
    List<CustomerSyncEntity> getCustomerSyncEntities();

}