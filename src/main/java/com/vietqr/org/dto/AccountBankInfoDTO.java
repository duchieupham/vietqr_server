package com.vietqr.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBankInfoDTO {
    @JsonProperty(value = "bankAccount")
    private String bankAccount;
    @JsonProperty(value = "bankShortName")
    private String bankShortName;
    @JsonProperty(value = "userBankName")
    private String userBankName;
    @JsonProperty(value = "mmsActive")
    private Boolean mmsActive;

    public AccountBankInfoDTO() {
        this.bankAccount = "";
        this.bankShortName = "";
        this.userBankName = "";
        this.mmsActive = false;
    }

    public AccountBankInfoDTO(String bankAccount, String bankShortName, String userBankName) {
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.mmsActive = false;
    }

    public AccountBankInfoDTO(String bankAccount, String bankShortName, String userBankName, Boolean mmsActive) {
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.mmsActive = mmsActive;
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

    public Boolean getMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(Boolean mmsActive) {
        this.mmsActive = mmsActive;
    }
}
