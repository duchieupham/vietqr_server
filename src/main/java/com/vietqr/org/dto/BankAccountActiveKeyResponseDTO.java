package com.vietqr.org.dto;

public class BankAccountActiveKeyResponseDTO {
    private String bankId;
    private String bankAccount;
    private String userBankName;
    private String bankShortName;
    private String bankCode;
    private String phoneAuthenticated;
    private String userId;

    public BankAccountActiveKeyResponseDTO() {
    }

    public BankAccountActiveKeyResponseDTO(String bankId, String bankAccount, String userBankName, String bankShortName, String bankCode, String phoneAuthenticated, String userId) {
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.bankCode = bankCode;
        this.phoneAuthenticated = phoneAuthenticated;
        this.userId = userId;
    }


    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
