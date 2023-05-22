package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBranchBranchInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String bankId;
    private String businessId;
    private String branchId;

    public AccountBranchBranchInsertDTO() {
        super();
    }

    public AccountBranchBranchInsertDTO(String userId, String bankId, String businessId, String branchId) {
        this.userId = userId;
        this.bankId = bankId;
        this.businessId = businessId;
        this.branchId = branchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Override
    public String toString() {
        return "AccountBranchBranchInsertDTO [userId=" + userId + ", bankId=" + bankId + ", businessId=" + businessId
                + ", branchId=" + branchId + "]";
    }

}
