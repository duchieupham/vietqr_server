package com.vietqr.org.dto;

public class AccountBankInfoDTO {
    private String bankAccount;
    private String bankShortName;
    private String userBankName;

    public AccountBankInfoDTO() {
        this.bankAccount = "";
        this.bankShortName = "";
        this.userBankName = "";
    }

    public AccountBankInfoDTO(String bankAccount, String bankShortName, String userBankName) {
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }
}
