package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import com.vietqr.org.dto.qrfeed.IUserRoleDTO;
import com.vietqr.org.dto.qrfeed.UserRoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrFolderUserService {

    void addUserIds(String qrFolderId, List<UserRoleDTO> userRoles, String userId);
    void addUserAdmin(String qrFolderId, String folderId, String userId);

    public List<IUserInFolderDTO> getUserInFolder(String qrFolderId);

    void updateUserRoles(String qrFolderId, List<UserRoleDTO> userRoles);

    List<IUserRoleDTO> getUserRolesByFolderId(String folderId);


    List<IUserRoleDTO> getUserRolesByFolderId(String folderId, String value, int offset, int size);

    int countUserRolesByFolderId(String folderId, String value);
    int countUsersFolder(String folderId);

    void updateUserRole(String folderId, String userId, String role);

    void deleteUserFromFolder(String folderId, String userId);

}
