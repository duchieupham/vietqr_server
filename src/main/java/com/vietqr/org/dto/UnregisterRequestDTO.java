package com.vietqr.org.dto;

import java.io.Serializable;

public class UnregisterRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String applicationType;

    public UnregisterRequestDTO() {
        super();
    }

    public UnregisterRequestDTO(String accountNumber, String applicationType) {
        this.accountNumber = accountNumber;
        this.applicationType = applicationType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

}
