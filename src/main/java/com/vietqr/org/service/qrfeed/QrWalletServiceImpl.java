package com.vietqr.org.service.qrfeed;

import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.repository.QrWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrWalletServiceImpl implements QrWalletService{
    @Autowired
    QrWalletRepository repo;

    @Override
    public int insertQrWallet(QrWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }
}
