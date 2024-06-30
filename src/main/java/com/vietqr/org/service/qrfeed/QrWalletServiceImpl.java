package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.dto.qrfeed.UserInfoLinkOrTextDTO;
import com.vietqr.org.dto.qrfeed.UserInfoVcardDTO;
import com.vietqr.org.dto.qrfeed.UserInfoVietQRDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.repository.QrWalletFolderRepository;
import com.vietqr.org.repository.QrWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class QrWalletServiceImpl implements QrWalletService {
    @Autowired
    QrWalletRepository repo;

    @Autowired
    QrWalletFolderRepository qrWalletFolderRepository;

    @Override
    public int insertQrWallet(QrWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public int countQrWallet(String value) {
        return repo.countQrWallet(value);
    }

    @Override
    public void updateQrWallet(String id, String description, int isPublic, String qrType, String title, String content) {
        repo.updateQrWallet(id, description, isPublic, qrType, title, content);
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
        return  qrWalletFolderRepository.findQrWalletIdsByQrFolderId(folderId);
    }

    @Override
    public void deleteQrItemsByIds(List<String> ids) {
        repo.deleteByQrWalletIds(ids);
    }
}
