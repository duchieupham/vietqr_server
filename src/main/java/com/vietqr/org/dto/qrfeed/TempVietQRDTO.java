package com.vietqr.org.dto.qrfeed;

import com.google.gson.Gson;

import java.io.Serializable;

public class TempVietQRDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String amount;
    private String content;
    private String value;

    public TempVietQRDTO() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
