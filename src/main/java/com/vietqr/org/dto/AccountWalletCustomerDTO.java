package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountWalletCustomerDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String amount;
    private boolean enableService;

    public AccountWalletCustomerDTO() {
        super();
    }

    public AccountWalletCustomerDTO(String amount, boolean enableService) {
        this.amount = amount;
        this.enableService = enableService;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }

}
