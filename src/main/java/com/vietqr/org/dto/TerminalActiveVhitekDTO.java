package com.vietqr.org.dto;

import java.io.Serializable;

public class TerminalActiveVhitekDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String mid;
    private String address;
    private String userIdVhitek;
    private String bankAccount;
    private String bankCode;
    private String userBankName;
    private String bankId;

    public TerminalActiveVhitekDTO() {
        super();
    }

    public TerminalActiveVhitekDTO(String userId, String mid, String address, String userIdVhitek,
            String bankAccount, String bankCode, String userBankName, String bankId) {
        this.userId = userId;
        this.mid = mid;
        this.address = address;
        this.userIdVhitek = userIdVhitek;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserIdVhitek() {
        return userIdVhitek;
    }

    public void setUserIdVhitek(String userIdVhitek) {
        this.userIdVhitek = userIdVhitek;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

}
