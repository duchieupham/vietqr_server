package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountCusBankRemoveDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;
    private String customerSyncId;

    public AccountCusBankRemoveDTO() {
        super();
    }

    public AccountCusBankRemoveDTO(String bankId, String customerSyncId) {
        this.bankId = bankId;
        this.customerSyncId = customerSyncId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

}
