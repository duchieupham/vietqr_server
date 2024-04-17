package com.vietqr.org.dto;

import java.io.Serializable;

public class RequestBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String nationalId;
    private String accountNumber;
    private String accountName;
    private String phoneNumber;
    // private String authenType;
    private String applicationType;
    // private String transType;

    public RequestBankDTO() {
        super();
    }

    public RequestBankDTO(String nationalId, String accountNumber, String accountName, String phoneNumber,
            String applicationType) {
        this.nationalId = nationalId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.phoneNumber = phoneNumber;
        // this.authenType = authenType;
        this.applicationType = applicationType;
        // this.transType = transType;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // public String getAuthenType() {
    // return authenType;
    // }

    // public void setAuthenType(String authenType) {
    // this.authenType = authenType;
    //
    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    // public String getTransType() {
    // return transType;
    // }

    // public void setTransType(String transType) {
    // this.transType = transType;
    // }

}
