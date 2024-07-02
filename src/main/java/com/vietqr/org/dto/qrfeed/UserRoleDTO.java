package com.vietqr.org.dto.qrfeed;

public class UserRoleDTO {
    public String userId;
    public String role;

    public UserRoleDTO(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public UserRoleDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
