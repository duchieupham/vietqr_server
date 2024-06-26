package com.vietqr.org.service.qrfeed;

import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.stereotype.Service;

@Service
public interface QrWalletService {

    public int insertQrWallet(QrWalletEntity entity);
}
