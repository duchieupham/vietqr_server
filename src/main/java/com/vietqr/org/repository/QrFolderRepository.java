package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrFolderRepository extends JpaRepository<QrFolderEntity, String> {
}
