package com.vietqr.org.dto;

import java.io.Serializable;

public class RequestLinkedBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String nationalId;
    private String accountNumber;
    private String accountName;
    private String phoneNumber;
    private String applicationType;
    private String bankCode;
    private String merchantName;

    public RequestLinkedBankDTO() {
        super();
    }

    public RequestLinkedBankDTO(String nationalId, String accountNumber, String accountName, String phoneNumber,
            String applicationType, String bankCode) {
        this.nationalId = nationalId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.phoneNumber = phoneNumber;
        this.applicationType = applicationType;
        this.bankCode = bankCode;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    @Override
    public String toString() {
        return "RequestLinkedBankDTO [nationalId=" + nationalId + ", accountNumber=" + accountNumber
                + ", accountName=" + accountName + ", phoneNumber=" + phoneNumber + ", applicationType=" + applicationType
                + ", bankCode=" + bankCode + ", merchantName=" + merchantName + "]";
    }
}
