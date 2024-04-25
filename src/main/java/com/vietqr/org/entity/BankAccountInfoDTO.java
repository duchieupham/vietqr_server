package com.vietqr.org.entity;

public class BankAccountInfoDTO {
    private String bankAccount;
    private String userBankName;
    private String bankShortName;

    public BankAccountInfoDTO() {
    }

    public BankAccountInfoDTO(String bankAccount, String userBankName, String bankShortName) {
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
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
}
