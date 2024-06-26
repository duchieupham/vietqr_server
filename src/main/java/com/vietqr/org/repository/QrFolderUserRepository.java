package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrFolderUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrFolderUserRepository extends JpaRepository<QrFolderUserEntity, String> {
}
