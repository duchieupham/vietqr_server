package com.vietqr.org.dto;

public class BankAccountInvoiceDTO {
    private String bankId;
    private String merchantId;
    private String userBankName;
    private String phoneNo;
    private String email;
    private String bankShortName;
    private String bankAccount;
    private String connectionType;
    private String feePackage;

    public BankAccountInvoiceDTO() {
    }

    public BankAccountInvoiceDTO(String bankId, String merchantId, String userBankName, String phoneNo, String email,
                                 String bankShortName, String bankAccount, String connectionType, String feePackage) {
        this.bankId = bankId;
        this.merchantId = merchantId;
        this.userBankName = userBankName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.bankShortName = bankShortName;
        this.bankAccount = bankAccount;
        this.connectionType = connectionType;
        this.feePackage = feePackage;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
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

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getFeePackage() {
        return feePackage;
    }

    public void setFeePackage(String feePackage) {
        this.feePackage = feePackage;
    }
}
