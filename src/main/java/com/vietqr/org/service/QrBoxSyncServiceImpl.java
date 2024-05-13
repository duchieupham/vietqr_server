package com.vietqr.org.service;

import com.vietqr.org.entity.QrBoxSyncEntity;
import com.vietqr.org.repository.QrBoxSyncRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrBoxSyncServiceImpl implements QrBoxSyncService {

    @Autowired
    private QrBoxSyncRepository repo;
    @Override
    public String checkExistQRBoxCode(String code) {
        return repo.checkExistQRBoxCode(code);
    }

    @Override
    public void insert(QrBoxSyncEntity entity) {
        repo.save(entity);
    }

    @Override
    public String getByQrCertificate(String qrCertificate) {
        return repo.getByQrCertificate(qrCertificate);
    }
}
