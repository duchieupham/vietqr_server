package com.vietqr.org.service.qrfeed;

import com.vietqr.org.repository.QrFolderUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class QrFolderUserServiceImpl implements QrFolderUserService {

    @Autowired
    QrFolderUserRepository repo;

    @Transactional
    public void addUserIds(String qrFolderId, List<String> userIds) {
        for (String userId : userIds) {
            String id = UUID.randomUUID().toString();
            repo.insertQrWalletFolder(id, qrFolderId, userId);
        }
    }
}
