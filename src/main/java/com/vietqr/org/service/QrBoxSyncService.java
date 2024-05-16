package com.vietqr.org.service;

import com.vietqr.org.entity.QrBoxSyncEntity;
import org.springframework.stereotype.Service;

@Service
public interface QrBoxSyncService {
    String checkExistQRBoxCode(String code);

    void insert(QrBoxSyncEntity entity);

    String getByQrCertificate(String qrCertificate);

    QrBoxSyncEntity getByMacAddress(String macAddr);
}
