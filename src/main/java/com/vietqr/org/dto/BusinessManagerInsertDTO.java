package com.vietqr.org.dto;

import java.io.Serializable;

public class BusinessManagerInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private int role;
    private String businessId;

    public BusinessManagerInsertDTO() {
        super();
    }

    public BusinessManagerInsertDTO(String userId, int role, String businessId) {
        this.userId = userId;
        this.role = role;
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

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

}
