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
    public void addQrWalletIds(String qrFolderId, List<String> qrWalletIds) {
        for (String qrWalletId : qrWalletIds) {
            String id = UUID.randomUUID().toString();
            repo.insertQrWalletFolder(id, qrFolderId, qrWalletId);
        }
    }


}
