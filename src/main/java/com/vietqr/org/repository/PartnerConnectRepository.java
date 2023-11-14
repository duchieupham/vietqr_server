package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.PartnerConnectEntity;

@Repository
public interface PartnerConnectRepository extends JpaRepository<PartnerConnectEntity, Long> {

    @Query(value = "SELECT * FROM partner_connect WHERE service = :service ", nativeQuery = true)
    PartnerConnectEntity getPartnerConnectByServiceName(@Param(value = "service") String service);
}
