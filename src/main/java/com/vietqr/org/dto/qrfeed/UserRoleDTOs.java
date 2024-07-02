package com.vietqr.org.dto.qrfeed;

public class UserRoleDTOs implements IUserRoleDTO {
    private String userId;
    private String role;

    public UserRoleDTOs(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getRole() {
        return null;
    }
}