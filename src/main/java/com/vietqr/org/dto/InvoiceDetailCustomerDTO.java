package com.vietqr.org.dto;

public class InvoiceDetailCustomerDTO {
    private String merchantId;
    private String merchantName;
    private String bankShortName;
    private String bankId;
    private String bankAccount;
    private String userBankName;
    private String phoneNo;
    private String email;
    private String connectionType;
    private String feePackage;
    private double vat;

    public InvoiceDetailCustomerDTO() {
    }

    public InvoiceDetailCustomerDTO(String merchantId, String merchantName, String bankShortName,
                                    String bankId, String bankAccount, String userBankName,
                                    String phoneNo, String email, String connectionType, String feePackage,
                                    double vat) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.bankShortName = bankShortName;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.connectionType = connectionType;
        this.feePackage = feePackage;
        this.vat = vat;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
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

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
}
