package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrWalletFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrWalletFolderRepository extends JpaRepository<QrWalletFolderEntity, String> {
}
