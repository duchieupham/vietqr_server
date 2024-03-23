package com.vietqr.org.repository;

import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransReceiveRequestMappingRepository extends JpaRepository<TransReceiveRequestMappingEntity, String> {
}
