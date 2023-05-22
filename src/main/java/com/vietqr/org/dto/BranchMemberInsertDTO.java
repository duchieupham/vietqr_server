package com.vietqr.org.dto;

import java.io.Serializable;

public class BranchMemberInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String branchId;
    private String businessId;
    private String userId;
    private int role;

    public BranchMemberInsertDTO() {
        super();
    }

    public BranchMemberInsertDTO(String branchId, String businessId, String userId, int role) {
        this.branchId = branchId;
        this.businessId = businessId;
        this.userId = userId;
        this.role = role;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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
