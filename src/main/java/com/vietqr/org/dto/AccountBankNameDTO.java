package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankNameDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String accountName;
    private String customerName;
    private String customerShortName;

    public AccountBankNameDTO() {
        super();
    }

    public AccountBankNameDTO(String accountName, String customerName, String customerShortName) {
        this.accountName = accountName;
        this.customerName = customerName;
        this.customerShortName = customerShortName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerShortName() {
        return customerShortName;
    }

    public void setCustomerShortName(String customerShortName) {
        this.customerShortName = customerShortName;
    }

}
