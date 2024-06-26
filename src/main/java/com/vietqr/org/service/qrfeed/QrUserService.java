package com.vietqr.org.service.qrfeed;

import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.stereotype.Service;

@Service
public interface QrUserService {
    public int insertQrUser(QrUserEntity entity);
}
