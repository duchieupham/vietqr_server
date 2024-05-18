package com.vietqr.org.repository;

import com.vietqr.org.entity.FeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeePackageRepository extends JpaRepository<FeePackageEntity, String> {
}
