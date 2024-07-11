package com.vietqr.org.service.qrfeed;

import com.vietqr.org.repository.QrWalletFolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QrWalletFolderServiceImpl implements QrWalletFolderService {

    @Autowired
    QrWalletFolderRepository repo;

    @Override
    public void addQrWalletIds(String qrFolderId, List<String> qrWalletIds, String userId) {
        for (String qrWalletId : qrWalletIds) {
            String id = UUID.randomUUID().toString();
            repo.insertQrWalletFolder(id, qrFolderId, qrWalletId);
        }
    }

    @Override
    public void addQrWalletsToFolder(String folderId, List<String> qrIds) {
        for (String qrWalletId : qrIds) {
            String id = UUID.randomUUID().toString();
            repo.insertQrWalletFolder(id, folderId, qrWalletId);
        }
    }

    @Override
    public void addQrWalletsInFolder( String folderId, List<String> qrIds) {
        for (String qrWalletId : qrIds) {
            UUID idgens = UUID.randomUUID();
            repo.addQrWalletFolder(idgens.toString(), folderId, qrWalletId);
        }
    }

    @Override
    public int countQrFolder(String folderId) {
        return repo.countQrFolder(folderId);
    }

    @Override
    public List<String> checkQrExists(String folderId, String userId) {
        return repo.checkQrExists(folderId, userId);
    }

    @Override
    public List<String> getToInsert(String folderId, String userId) {
        return repo.getToInsert(folderId, userId);
    }

    @Override
    public void deleteQrsInFolder(List<String> qrIds) {
        repo.deleteQrsInFolder(qrIds);
    }

    @Override
    public List<String> getListQrsInFolder(String folderId, String userId, List<String> qrIds) {
        return repo.getListQrsInFolder(folderId, userId, qrIds);
    }

}
