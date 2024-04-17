package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CarrierTypeEntity;

@Repository
public interface CarrierTypeRepository extends JpaRepository<CarrierTypeEntity, Long> {

    @Query(value = "SELECT * FROM carrier_type ", nativeQuery = true)
    List<CarrierTypeEntity> getCarrierTypes();

    @Query(value = "SELECT * FROM carrier_type WHERE id = :id", nativeQuery = true)
    CarrierTypeEntity getCarrierTypeById(@Param(value = "id") String id);
}
