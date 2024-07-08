package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import com.vietqr.org.dto.qrfeed.IUserRoleDTO;
import com.vietqr.org.dto.qrfeed.UserRoleDTO;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.repository.QrFolderUserRepository;
import com.vietqr.org.repository.QrUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class QrFolderUserServiceImpl implements QrFolderUserService {

    @Autowired
    private QrFolderUserRepository repo;
    @Autowired
    private QrUserRepository qrUserRepository;
    @Autowired
    private QrFolderUserRepository qrFolderUserRepository;


//    @Transactional
//    public void addUserIds(String qrFolderId, List<String> userIds, String userId) {
//        for (String userIdDto : userIds) {
//            String id = UUID.randomUUID().toString();
//            repo.insertUserToFolder(id, qrFolderId, userIdDto, userId);
//        }
//    }

    @Override
    public void addUserIds(String qrFolderId, List<UserRoleDTO> userRoles, String userId) {
        for (UserRoleDTO userRole : userRoles) {
            String id = UUID.randomUUID().toString();
            repo.insertQrWalletFolder(id, qrFolderId, userRole.getUserId());

            // Add user with role to qr_user table
            QrUserEntity qrUserEntity = new QrUserEntity();
            qrUserEntity.setId(UUID.randomUUID().toString());
            qrUserEntity.setQrWalletId("");
            qrUserEntity.setQrFolderId(qrFolderId);
            qrUserEntity.setUserId(userRole.getUserId());
            qrUserEntity.setRole(userRole.getRole());
            qrUserRepository.save(qrUserEntity);
        }
    }

    @Override
    public void addUserAdmin(String qrFolderId, String folderId, String userId) {
            repo.addUserAdmin(qrFolderId, folderId, userId);
    }

    @Override
    public List<IUserInFolderDTO> getUserInFolder(String qrFolderId) {
        return repo.getUserInFolder(qrFolderId);
    }

    @Override
    public void updateUserRoles(String qrFolderId, List<UserRoleDTO> userRoles) {
        for (UserRoleDTO userRole : userRoles) {
            qrUserRepository.updateUserRole(userRole.getUserId(), qrFolderId, userRole.getRole());
        }
    }

    @Override
    public List<IUserRoleDTO> getUserRolesByFolderId(String folderId) {
        return repo.findUserRolesByFolderId(folderId);
    }

    @Override
    public List<IUserRoleDTO> getUserRolesByFolderId(String folderId, String value, int offset, int size) {
        return repo.findUserRolesByFolderId(folderId, value, offset, size);
    }

    @Override
    public int countUserRolesByFolderId(String folderId, String value) {
        return repo.countUserRolesByFolderId(folderId, value);
    }

    @Override
    public int countUsersFolder(String folderId) {
        return repo.countUsersFolder(folderId);
    }

    @Override
    public void updateUserRole(String folderId, String userId, String role) {
        qrUserRepository.updateSingleUserRole(userId, folderId, role);
    }

    @Override
    public void deleteUserFromFolder(String folderId, String userId) {
        qrFolderUserRepository.deleteUserFromFolder(folderId, userId);
        qrUserRepository.deleteUserRole(folderId, userId);
    }

}
