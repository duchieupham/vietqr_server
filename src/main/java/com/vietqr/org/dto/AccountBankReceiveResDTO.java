package com.vietqr.org.dto;

public class AccountBankReceiveResDTO {
    boolean isMmsActive;
    String bankAccount;
    String bankId;
    String userBankName;
    String bankShortName;

    public boolean getIsMmsActive() {
        return isMmsActive;
    }

    public void setIsMmsActive(boolean mmsActive) {
        isMmsActive = mmsActive;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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
}
