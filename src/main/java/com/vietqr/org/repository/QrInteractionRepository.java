package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrInteractionRepository extends JpaRepository<QrInteractionEntity, String> {
}
