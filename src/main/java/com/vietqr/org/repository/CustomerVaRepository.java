package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Repository
public interface CustomerVaRepository extends JpaRepository<CustomerVaEntity, Long> {

    @Query(value = "SELECT COUNT(id) FROM customer_va", nativeQuery = true)
    Long getCustomerVaLength();
}
