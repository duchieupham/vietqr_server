package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankReceiveRpaDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankCode;
    private String bankAccount;
    private String userBankName;
    private String userId;
    private String username;
    private String password;

    public AccountBankReceiveRpaDTO() {
        super();
    }

    public AccountBankReceiveRpaDTO(String bankCode, String bankAccount, String userBankName, String userId,
            String username, String password) {
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
