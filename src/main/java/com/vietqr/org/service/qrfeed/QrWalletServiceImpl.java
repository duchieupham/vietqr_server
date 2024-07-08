package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.dto.qrfeed.IQrWalletDTO;
import com.vietqr.org.dto.qrfeed.IQrWalletPrivateDTO;
import com.vietqr.org.dto.qrfeed.QrCommentDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.repository.QrCommentRepository;
import com.vietqr.org.repository.QrWalletFolderRepository;
import com.vietqr.org.repository.QrWalletRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QrWalletServiceImpl implements QrWalletService {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(QrWalletServiceImpl.class);
    @Autowired
    QrWalletRepository repo;

    @Autowired
    QrWalletFolderRepository qrWalletFolderRepository;

    @Autowired
    QrCommentRepository qrCommentRepository;

    @Override
    public void updateFileQrById(String id, String qrId) {
        repo.updateFileQrById(id, qrId);
    }

    @Override
    public void updateLogoQrWallet(String qrId) {
        repo.updateLogoQrWallet(qrId);
    }

    @Override
    public int insertQrWallet(QrWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public int countQrWallet(String value) {
        return repo.countQrWallet(value);
    }

    @Override
    public int countQrWalletPublic(String value) {
        return repo.countQrWalletPublic(value);
    }

    @Override
    public void updateQrWallet(String id, String description, int isPublic, int qrType, String title, String content, int style, int theme) {
        repo.updateQrWallet(id, description, isPublic, qrType, title, content, style, theme);
    }

    @Override
    public void updateQrVCard(String id, String description, int isPublic, int qrType, String title, String value, int style, int theme) {
        repo.updateQrVCard(id, description, isPublic, qrType, title, value, style, theme);
    }

    @Override
    public void updateQrVietQR(String id, String description, int isPublic, int qrType, String title, String value, int style, int theme) {
        repo.updateQrVietQR(id, description, isPublic, qrType, title, value, style, theme);
    }

    @Override
    public QrWalletEntity getQrVCardUpdate(String qrId) {
        return repo.getQrVCardUpdate(qrId);
    }

    @Override
    public QrWalletEntity getQrVietQR(String qrId) {
        return repo.getQrVietQR(qrId);
    }

    @Override
    public IListQrWalletDTO getQrLinkOrQrTextByUserId(String userId) {
        return repo.getQrLinkOrQrTextByUserId(userId);
    }

    @Override
    public QrWalletEntity getQrLinkOrQrTextById(String qrId) {
        return repo.getQrLinkOrQrTextById(qrId);
    }

    @Override
    public List<IListQrWalletDTO> getQrWallets(String value, int offset, int size) {
        return repo.getQrWallets(value, offset, size);
    }

    @Override
    public List<IListQrWalletDTO> getQrWalletNoPaging(String value, int type) {
        return repo.getQrWalletNoPaging(value, type);
    }

    @Override
    public List<IListQrWalletDTO> getQrWalletNoPagingAll(String folderId) {
        return repo.getQrWalletNoPagingAll(folderId);
    }

    @Override
    public List<IListQrWalletDTO> getQrWalletPublic(String value, int offset, int size) {
        return repo.getQrWalletPublic(value, offset, size);
    }

    @Override
    public void deleteQrWalletsByIds(List<String> ids) {
        List<String> existingIds = repo.findExistingIds(ids);

        List<String> missingIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("The following IDs do not exist: " + missingIds);
        }

        repo.deleteByIds(existingIds);
    }

    @Override
    public List<String> findExistingIds(List<String> ids) {
        return repo.findExistingIds(ids);
    }

    @Override
    public List<String> getUserLinkOrTextData(String folderId, int type) {
        if (type == -1) {
            return repo.getUserDataWithoutType(folderId, "");
        }
        return repo.getUserDataWithType(folderId, type);
    }

    @Override
    public List<String> getQrData(String folderId, int type) {
        if (type == -1) {
            return repo.getQrDataWithoutType(folderId, "");
        }
        return repo.getQrDataWithType(folderId, type);
    }

    @Override
    public List<String> getUserVCardData(String folderId, int type) {
        return repo.getUserVCardData(folderId, type);
    }

    @Override
    public List<String> getUserVietQrData(String folderId, int type) {
        return repo.getUserVietQrData(folderId, type);
    }

    @Override
    public int countUserLinkOrTextInfo(String folderId, int type) {
        return repo.countUserLinkOrTextInfo(folderId, type);
    }

    @Override
    public int countUserVCardInfo(String folderId, int type) {
        return repo.countUserVCardInfo(folderId, type);
    }

    @Override
    public int countUserVietQrInfo(String folderId, int type) {
        return repo.countUserVietQrInfo(folderId, type);
    }

    @Override
    public List<String> getQrWalletIdsByFolderId(String folderId) {
        return qrWalletFolderRepository.findQrWalletIdsByQrFolderId(folderId);
    }

    @Override
    public void deleteQrItemsByIds(List<String> ids) {
        repo.deleteByQrWalletIds(ids);
    }


    @Override
    public List<IQrWalletDTO> getAllPublicQrWallets(String userId, int offset, int size) {
        List<IQrWalletDTO> qrWallets = repo.findAllPublicQrWallets(userId, offset, size);
        for (IQrWalletDTO wallet : qrWallets) {
            logger.info("QR Wallet ID: " + wallet.getId() + ", hasLiked: " + wallet.getHasLiked());
        }
        return qrWallets;
    }

    @Override
    public int countPublicQrWallets() {
        return repo.countPublicQrWallets();
    }


    @Override
    public IQrWalletDTO getQrWalletDetailsById(String userId, String qrWalletId) {
        return repo.findQRWalletDetailsById(userId, qrWalletId);
    }

    @Override
    public Page<QrCommentDTO> findCommentsByQrWalletId(String qrWalletId, Pageable pageable) {
        return repo.findCommentsByQrWalletId(qrWalletId, pageable);
    }

    @Override
    public int countCommentsByQrWalletId(String qrWalletId) {
        return repo.countCommentsByQrWalletId(qrWalletId);
    }

    @Override
    public List<IQrWalletPrivateDTO> getAllPrivateQrWallets(String userId, String value) {
        return repo.findAllPrivateQrWallets(userId, value);
    }

    @Override
    public List<IQrWalletPrivateDTO> getQrTextPrivate(String userId, String value) {
        return repo.getQrTextPrivate(userId, value);
    }

    @Override
    public List<IQrWalletPrivateDTO> getQrVCardPrivate(String userId, String value) {
        return repo.getQrVCardPrivate(userId, value);
    }

    @Override
    public List<IQrWalletPrivateDTO> getQrVietQrPrivate(String userId, String value) {
        return repo.getQrVietQrPrivate(userId, value);
    }

    @Override
    public List<IQrWalletPrivateDTO> getQrLinkPrivate(String userId, String value) {
        return repo.getQrLinkPrivate(userId, value);
    }

    @Override
    public int countPrivateQrWallets(String userId, String value) {
        return repo.countPrivateQrWallets(userId, value);
    }

    @Override
    public int countQrLinkPrivate(String userId, String value) {
        return repo.countQrLinkPrivate(userId, value);
    }

    @Override
    public int countQrTextPrivate(String userId, String value) {
        return repo.countQrTextPrivate(userId, value);
    }

    @Override
    public int countQrVCardPrivate(String userId, String value) {
        return repo.countQrVCardPrivate(userId, value);
    }

    @Override
    public int countQrVietQrPrivate(String userId, String value) {
        return repo.countQrVietQrPrivate(userId, value);
    }

    @Override
    public List<IQrWalletDTO> getQrWalletsByPublicStatus(String userId, int isPublic, int offset, int size) {
        return repo.findQrWalletsByPublicStatus(userId, isPublic, offset, size);
    }

    @Override
    public int countQrWalletsByPublicStatus(int isPublic) {
        return repo.countQrWalletsByPublicStatus(isPublic);
    }

    @Override
    public QrWalletEntity getQrWalletById(String qrWalletId) {
        return repo.getQrWalletById(qrWalletId);
    }


}
