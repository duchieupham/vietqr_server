package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionBranchInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String businessId;
    private String branchId;
    private int offset;

    public TransactionBranchInputDTO() {
        super();
    }

    public TransactionBranchInputDTO(String businessId, String branchId, int offset) {
        this.businessId = businessId;
        this.branchId = branchId;
        this.offset = offset;
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
