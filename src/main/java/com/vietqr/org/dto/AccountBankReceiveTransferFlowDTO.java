package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankReceiveTransferFlowDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;
    private String customerSyncId;

    public AccountBankReceiveTransferFlowDTO() {
        super();
    }

    public AccountBankReceiveTransferFlowDTO(String bankId, String customerSyncId) {
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
