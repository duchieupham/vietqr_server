package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankUnauthenticatedDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankTypeId;
    private String bankAccount;
    private String userBankName;
    private String userId;
    private String nationalId;
    private String phoneAuthenticated;

    public AccountBankUnauthenticatedDTO() {
        super();
    }

    public AccountBankUnauthenticatedDTO(String bankTypeId, String bankAccount, String userBankName, String userId,
            String nationalId, String phoneAuthenticated) {
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.userId = userId;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;

    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "AccountBankUnauthenticatedDTO [bankTypeId=" + bankTypeId + ", bankAccount=" + bankAccount
                + ", userBankName=" + userBankName + ", userId=" + userId + ", nationalId=" + nationalId
                + ", phoneAuthenticated=" + phoneAuthenticated + "]";
    }

}
