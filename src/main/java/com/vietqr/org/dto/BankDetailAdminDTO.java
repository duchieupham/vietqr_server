package com.vietqr.org.dto;

public class BankDetailAdminDTO {
    private String bankId;
    private String merchantId;
    private String userBankName;
    private String bankAccount;
    private String bankShortName;
    private String phoneNo;
    private String email;
    private String connectionType;
    private String feePackage;
    private double vat;
    private int transFee1;
    private double transFee2;
    private int transRecord;

    public BankDetailAdminDTO() {
    }

    public BankDetailAdminDTO(String bankId, String merchantId,
                              String userBankName, String bankAccount,
                              String bankShortName, String phoneNo,
                              String email, String connectionType,
                              String feePackage, double vat,
                              int transFee1, double transFee2,
                              int transRecord) {
        this.bankId = bankId;
        this.merchantId = merchantId;
        this.userBankName = userBankName;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.connectionType = connectionType;
        this.feePackage = feePackage;
        this.vat = vat;
        this.transFee1 = transFee1;
        this.transFee2 = transFee2;
        this.transRecord = transRecord;
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

    public int getTransFee1() {
        return transFee1;
    }

    public void setTransFee1(int transFee1) {
        this.transFee1 = transFee1;
    }

    public double getTransFee2() {
        return transFee2;
    }

    public void setTransFee2(double transFee2) {
        this.transFee2 = transFee2;
    }

    public int getTransRecord() {
        return transRecord;
    }

    public void setTransRecord(int transRecord) {
        this.transRecord = transRecord;
    }
}
