package com.vietqr.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantBankMapperDTO {
    @JsonProperty(value = "vso")
    private String vso;
    @JsonProperty(value = "merchantName")
    private String merchantName;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "phoneNo")
    private String phoneNo;
    @JsonProperty(value = "bankAccount")
    private String bankAccount;
    @JsonProperty(value = "userBankName")
    private String userBankName;
    @JsonProperty(value = "bankShortName")
    private String bankShortName;

    public MerchantBankMapperDTO() {
        this.vso = "";
        this.merchantName = "";
        this.email = "";
        this.phoneNo = "";
        this.bankAccount = "";
        this.userBankName = "";
        this.bankShortName = "";
    }

    public MerchantBankMapperDTO(String vso, String merchantName, String email, String phoneNo,
                                 String bankAccount, String userBankName, String bankShortName) {
        this.vso = vso;
        this.merchantName = merchantName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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
