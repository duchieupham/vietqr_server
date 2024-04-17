package com.vietqr.org.dto;

import java.io.Serializable;

public class CustomerSyncStatusDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String customerSyncId;
    private int status;

    public CustomerSyncStatusDTO() {
        super();
    }

    public CustomerSyncStatusDTO(String customerSyncId, int status) {
        this.customerSyncId = customerSyncId;
        this.status = status;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
