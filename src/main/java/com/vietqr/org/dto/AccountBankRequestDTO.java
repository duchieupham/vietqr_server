package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String nationalId;
    private String phoneAuthenticated;
    private int requestType;
    private String address;
    private String userId;

    public AccountBankRequestDTO() {
        super();
    }

    public AccountBankRequestDTO(String bankAccount, String userBankName, String bankCode, String nationalId,
            String phoneAuthenticated, int requestType, String address, String userId) {
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.requestType = requestType;
        this.address = address;
        this.userId = userId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
