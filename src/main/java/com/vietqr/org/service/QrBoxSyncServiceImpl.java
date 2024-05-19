package com.vietqr.org.service;

import com.vietqr.org.dto.ITidInternalDTO;
import com.vietqr.org.entity.QrBoxSyncEntity;
import com.vietqr.org.repository.QrBoxSyncRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public QrBoxSyncEntity getByMacAddress(String macAddr) {
        return repo.getByMacAddress(macAddr);
    }

    @Override
    public List<ITidInternalDTO> getQrBoxSyncByBankAccount(String value, int offset, int size) {
        return repo.getQrBoxSyncByBankAccount(value, offset, size);
    }

    @Override
    public int countQrBoxSyncByBankAccount(String value) {
        return repo.countQrBoxSyncByBankAccount(value);
    }

    @Override
    public List<ITidInternalDTO> getQrBoxSync(int offset, int size) {
        return repo.getQrBoxSync(offset, size);
    }

    @Override
    public int countQrBoxSync() {
        return repo.countQrBoxSync();
    }

    @Override
    public void updateQrBoxSync(String qrCertificate, long currentDateTimeUTC, boolean active, String name) {
        repo.updateQrBoxSync(qrCertificate, currentDateTimeUTC, active, name);
    }
}
