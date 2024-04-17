package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRCreateUnauthenticatedDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String bankCode;
    private String userBankName;
    private String amount;
    private String content;

    public VietQRCreateUnauthenticatedDTO() {
        super();
    }

    public VietQRCreateUnauthenticatedDTO(String bankAccount, String bankCode, String userBankName, String amount,
            String content) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.amount = amount;
        this.content = content;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
