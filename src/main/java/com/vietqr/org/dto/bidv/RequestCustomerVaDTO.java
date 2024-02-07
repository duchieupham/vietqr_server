package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class RequestCustomerVaDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // private String merchantId;
    // private String merchantName;
    private String bankAccount;
    private String userBankName;
    private String nationalId;
    private String phoneAuthenticated;

    public RequestCustomerVaDTO() {
        super();
    }

    public RequestCustomerVaDTO(/* String merchantId, String merchantName, */ String bankAccount, String userBankName,
            String nationalId,
            String phoneAuthenticated) {
        // this.merchantId = merchantId;
        // this.merchantName = merchantName;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
    }

    // public String getMerchantId() {
    // return merchantId;
    // }

    // public void setMerchantId(String merchantId) {
    // this.merchantId = merchantId;
    // }

    // public String getMerchantName() {
    // return merchantName;
    // }

    // public void setMerchantName(String merchantName) {
    // this.merchantName = merchantName;
    // }

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

}
