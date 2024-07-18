package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrWalletCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrWalletCommentRepository extends JpaRepository<QrWalletCommentEntity, String> {
}
