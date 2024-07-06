package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public interface QrWalletService {

    public int insertQrWallet(QrWalletEntity entity);

    public int countQrWallet(String value);

    public void updateQrWallet(String id, String description, int isPublic, int qrType, String title, String content, int style, int theme);

    public IListQrWalletDTO getQrLinkOrQrTextByUserId(String userId);

    public QrWalletEntity getQrLinkOrQrTextById(String qrId);

    public List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);

    public void deleteQrWalletsByIds(List<String> ids);

    public List<String> findExistingIds(List<String> ids);
    public List<String> getUserLinkOrTextData(String folderId, int type);
    public List<String> getQrData(String folderId, int type);
    public List<String> getUserVCardData(String folderId, int type);
    public List<String> getUserVietQrData(String folderId, int type);

    public int countUserLinkOrTextInfo(String folderId, int type);
    public int countUserVCardInfo(String folderId, int type);
    public int countUserVietQrInfo(String folderId, int type);


    List<String> getQrWalletIdsByFolderId(String folderId);
    void deleteQrItemsByIds(List<String> ids);

    List<IQrWalletDTO> getAllPublicQrWallets(String userId, int offset, int size);
    int countPublicQrWallets();
    IQrWalletDTO getQrWalletDetailsById(String userId,String qrWalletId);

    Page<QrCommentDTO> findCommentsByQrWalletId(String qrWalletId, Pageable pageable);
    int countCommentsByQrWalletId(String qrWalletId);

    List<IQrWalletPrivateDTO> getAllPrivateQrWallets(String userId, int offset, int size);

    int countPrivateQrWallets();


    List<IQrWalletDTO> getQrWalletsByPublicStatus(String userId, int isPublic, int offset, int size);

    int countQrWalletsByPublicStatus(int isPublic);


    QrWalletEntity getQrWalletById(String qrWalletId);


}
