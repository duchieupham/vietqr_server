package com.vietqr.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountInfoDTO {
    @JsonProperty(value = "bankAccount")
    private String bankAccount;
    @JsonProperty(value = "userBankName")
    private String userBankName;
    @JsonProperty(value = "bankShortName")
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
