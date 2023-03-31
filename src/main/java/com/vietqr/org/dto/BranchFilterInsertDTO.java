package com.vietqr.org.dto;

import java.io.Serializable;

public class BranchFilterInsertDTO implements Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private String businessId;
    private String userId;
    private int role;

    public BranchFilterInsertDTO() {
        super();
    }

    public BranchFilterInsertDTO(String businessId, String userId, int role) {
        this.businessId = businessId;
        this.userId = userId;
        this.role = role;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}