package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
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
    public void addUserIds(String qrFolderId, List<String> userIds, String userId) {
        for (String userIdDto : userIds) {
            String id = UUID.randomUUID().toString();
            repo.insertUserToFolder(id, qrFolderId, userIdDto, userId);
        }
    }

    @Override
    public List<IUserInFolderDTO> getUserInFolder(String qrFolderId) {
        return repo.getUserInFolder(qrFolderId);
    }
}
