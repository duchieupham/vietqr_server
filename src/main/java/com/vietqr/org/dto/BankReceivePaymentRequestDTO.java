package com.vietqr.org.dto;

public class BankReceivePaymentRequestDTO {
    private String bankId;
    private String bankAccount;
    private String bankShortName;
    private String userBankName;
    private boolean isChecked;

    public BankReceivePaymentRequestDTO() {
    }

    public BankReceivePaymentRequestDTO(String bankId, String bankAccount, String bankShortName, String userBankName) {
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
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

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean checked) {
        isChecked = checked;
    }
}
