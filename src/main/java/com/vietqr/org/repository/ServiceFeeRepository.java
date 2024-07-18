package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.ServiceFeeEntity;

@Repository
public interface ServiceFeeRepository extends JpaRepository<ServiceFeeEntity, Long> {

    @Query(value = "SELECT * FROM service_fee WHERE sub = false AND active = true ", nativeQuery = true)
    List<ServiceFeeEntity> getServicesFee();

    @Query(value = "SELECT * FROM service_fee WHERE id = :id ", nativeQuery = true)
    ServiceFeeEntity getServiceFeeById(@Param(value = "id") String id);

    @Query(value = "SELECT * FROM service_fee WHERE sub = true AND active = true AND ref_id = :refId ", nativeQuery = true)
    List<ServiceFeeEntity> getSubServicesFee(@Param(value = "refId") String refId);

}
