package com.vietqr.org.service.qrfeed;

import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.repository.QrUserRepository;
import com.vietqr.org.repository.QrWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrUserServiceImpl implements QrUserService {
    @Autowired
    QrUserRepository repo;

    @Override
    public int insertQrUser(QrUserEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }
}
