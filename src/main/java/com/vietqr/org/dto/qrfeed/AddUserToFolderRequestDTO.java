package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class AddUserToFolderRequestDTO {
    public String folderId;
    public String userId;
    public List<UserRoleDTO> userRoles;

    public AddUserToFolderRequestDTO(String folderId, String userId, List<UserRoleDTO> userRoles) {
        this.folderId = folderId;
        this.userId = userId;
        this.userRoles = userRoles;
    }

    public AddUserToFolderRequestDTO() {
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserRoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoleDTO> userRoles) {
        this.userRoles = userRoles;
    }
}
