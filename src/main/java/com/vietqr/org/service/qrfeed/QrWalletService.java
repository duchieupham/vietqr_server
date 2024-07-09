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
    void updateFileQrById(String id, String qrId);

    void updateLogoQrWallet(String qrId);

    public int insertQrWallet(QrWalletEntity entity);

    public int countQrWallet(String value);

    public int countQrWalletPublic(String value);

    public void updateQrWallet(String id, String description, int isPublic, int qrType, String title, String content, int style, int theme);

    public void updateQrVCard(String id, String description, int isPublic, int qrType,
                              String title, String value, int style, int theme);

    public void updateQrVietQR(String id, String description, int isPublic, int qrType,
                               String title, String value, int style, int theme);

    public QrWalletEntity getQrVCardUpdate(String qrId);

    public QrWalletEntity getQrVietQR(String qrId);

    public IListQrWalletDTO getQrLinkOrQrTextByUserId(String userId);

    public QrWalletEntity getQrLinkOrQrTextById(String qrId);

    public List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);
    public List<IListQrWalletDTO> getQrWalletNoPaging(String value, int type);
    public List<IListQrWalletDTO> getQrWalletNoPagingAll(String folderId);

    public List<IListQrWalletDTO> getQrWalletPublic(String value, int offset, int size);

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

    IQrWalletDTO getQrWalletDetailsById(String userId, String qrWalletId);

    Page<QrCommentDTO> findCommentsByQrWalletId(String qrWalletId, Pageable pageable);

    int countCommentsByQrWalletId(String qrWalletId);

    List<IQrWalletPrivateDTO> getAllPrivateQrWallets(String userId, String value);

    List<IQrWalletPrivateDTO> getQrTextPrivate(String userId, String value);

    List<IQrWalletPrivateDTO> getQrVCardPrivate(String userId, String value);

    List<IQrWalletPrivateDTO> getQrVietQrPrivate(String userId, String value);

    List<IQrWalletPrivateDTO> getQrLinkPrivate(String userId, String value);

    int countPrivateQrWallets(String userId, String value);

    int countQrLinkPrivate(String userId, String value);

    int countQrTextPrivate(String userId, String value);

    int countQrVCardPrivate(String userId, String value);

    int countQrVietQrPrivate(String userId, String value);

    List<IQrWalletDTO> getQrWalletsByPublicStatus(String userId, int isPublic, int offset, int size);

    int countQrWalletsByPublicStatus(int isPublic);


    QrWalletEntity getQrWalletById(String qrWalletId);

    QrWalletEntity getQrWalletDetail(String qrWalletId);

}
