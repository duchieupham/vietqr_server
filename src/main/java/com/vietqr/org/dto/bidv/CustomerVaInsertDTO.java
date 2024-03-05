package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class CustomerVaInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String merchantId;
    private String merchantName;
    private String bankId;
    private String userId;
    private String bankAccount;
    private String userBankName;
    private String nationalId;
    private String phoneAuthenticated;
    private String vaNumber;

    public CustomerVaInsertDTO() {
        super();
    }

    public CustomerVaInsertDTO(String merchantId, String merchantName, String bankId, String userId, String bankAccount,
            String userBankName, String nationalId, String phoneAuthenticated, String vaNumber) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.bankId = bankId;
        this.userId = userId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.vaNumber = vaNumber;
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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getVaNumber() {
        return vaNumber;
    }

    public void setVaNumber(String vaNumber) {
        this.vaNumber = vaNumber;
    }

}
