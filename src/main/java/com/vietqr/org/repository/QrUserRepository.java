package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrUserRepository extends JpaRepository<QrUserEntity, String> {
}
