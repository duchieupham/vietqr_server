package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.UserRoleDTO;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.repository.QrUserRepository;
import com.vietqr.org.repository.QrWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QrUserServiceImpl implements QrUserService {
    @Autowired
    QrUserRepository repo;

    @Override
    public int insertQrUser(QrUserEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updateRoleUser(String folderId, String userId, String role) {
        repo.updateRoleUser(folderId, userId, role);
    }

    @Override
    public void deleteUserFromFolder(String folderId, String userId) {
        repo.deleteUserFromFolder(folderId, userId);
    }

    @Override
    public void updateRoleUser(String folderId, List<UserRoleDTO> userRoles) {
        for (UserRoleDTO userRole : userRoles) {
            repo.updateRoleUser(folderId, userRole.getUserId(), userRole.getRole());
        }
    }

    @Override
    public String checkRoleEdit(String userID, String folderId) {
        return repo.checkRoleEdit(userID, folderId);
    }
}
