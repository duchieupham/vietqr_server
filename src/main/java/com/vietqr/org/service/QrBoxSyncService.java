package com.vietqr.org.service;

import com.vietqr.org.dto.ITidInternalDTO;
import com.vietqr.org.entity.QrBoxSyncEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrBoxSyncService {
    String checkExistQRBoxCode(String code);

    void insert(QrBoxSyncEntity entity);

    String getByQrCertificate(String qrCertificate);

    QrBoxSyncEntity getByMacAddress(String macAddr);

    List<ITidInternalDTO> getQrBoxSyncByBankAccount(String value, int offset, int size);

    int countQrBoxSyncByBankAccount(String value);

    List<ITidInternalDTO> getQrBoxSync(int offset, int size);

    int countQrBoxSync();

    void updateQrBoxSync(String qrCertificate, long currentDateTimeUTC, boolean active, String name);
}
