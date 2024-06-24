package com.vietqr.org.dto;

public class BankAccountAdminUpdateDTO {
    private String bankId;
    private String bankAccount;
    private String bankShortName;
    private String email;
    private String midName;
    private String vso;

    public BankAccountAdminUpdateDTO(String bankId, String bankAccount, String bankShortName, String email, String midName, String vso) {
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.email = email;
        this.midName = midName;
        this.vso = vso;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }
}
