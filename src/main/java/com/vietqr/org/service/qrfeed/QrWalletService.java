package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public interface QrWalletService {

    public int insertQrWallet(QrWalletEntity entity);

    public int countQrWallet(String value);

    public void updateQrWallet(String id, String description, int isPublic, String qrType, String title, String content);

    public IListQrWalletDTO getQrLinkOrQrTextByUserId(String userId);

    public QrWalletEntity getQrLinkOrQrTextById(String qrId);

    public List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);

    public void deleteQrWalletsByIds(List<String> ids);

    public List<String> findExistingIds(List<String> ids);
}
