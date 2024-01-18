package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.ServicePartnerCheckerEntity;

@Repository
public interface ServicePartnerCheckerRepository extends JpaRepository<ServicePartnerCheckerEntity, Long> {

}