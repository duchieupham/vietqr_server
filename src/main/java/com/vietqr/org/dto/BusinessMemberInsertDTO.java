package com.vietqr.org.dto;

import java.io.Serializable;

public class BusinessMemberInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String role;

    public BusinessMemberInsertDTO() {
        super();
    }

    public BusinessMemberInsertDTO(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRole() {
        return Integer.parseInt(role);
    }

    public void setRole(String role) {
        this.role = role;
    }

}
