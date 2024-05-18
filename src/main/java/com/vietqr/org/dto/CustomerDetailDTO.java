package com.vietqr.org.dto;

public class CustomerDetailDTO {
    private String vso;
    private String merchantName;
    private String platform;
    private String bankShortName;
    private String bankAccount;
    private String userBankName;
    private String connectionType;
    private String phoneNo;
    private String email;

    public CustomerDetailDTO() {
    }

    public CustomerDetailDTO(String vso, String merchantName, String platform, String bankShortName,
                             String bankAccount, String userBankName, String connectionType, String phoneNo,
                             String email) {
        this.vso = vso;
        this.merchantName = merchantName;
        this.platform = platform;
        this.bankShortName = bankShortName;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.connectionType = connectionType;
        this.phoneNo = phoneNo;
        this.email = email;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
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

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
