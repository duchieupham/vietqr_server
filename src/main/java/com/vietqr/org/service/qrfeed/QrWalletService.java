package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrWalletService {

    public int insertQrWallet(QrWalletEntity entity);
    public int countQrWallet(String value);
    public List<IListQrWalletDTO> getQrWalletByUserId(String userId);
    public List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);
}
