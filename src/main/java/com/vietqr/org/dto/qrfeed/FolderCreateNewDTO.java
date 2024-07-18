package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class FolderCreateNewDTO {

    public String title;
    public String description;
    public String userId;

    public List<UserRoleDTO> userRoles;
    public List<String> qrIds;

    public FolderCreateNewDTO() {
    }

    public FolderCreateNewDTO(String title, String description, String userId, List<UserRoleDTO> userRoles, List<String> qrIds) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.userRoles = userRoles;
        this.qrIds = qrIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserRoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoleDTO> userRoles) {
        this.userRoles = userRoles;
    }

    public List<String> getQrIds() {
        return qrIds;
    }

    public void setQrIds(List<String> qrIds) {
        this.qrIds = qrIds;
    }

    public class UserRole {
        public String userId;
        public String role;
    }

}


