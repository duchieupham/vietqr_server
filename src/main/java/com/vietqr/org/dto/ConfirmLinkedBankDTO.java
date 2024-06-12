package com.vietqr.org.dto;

import java.io.Serializable;

public class ConfirmLinkedBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String otpValue;
    private String applicationType;
    private String bankCode;
    private String bankAccount;
    private String merchantId;
    private String merchantName;
    private String confirmId;
    private String otpNumber;

    public ConfirmLinkedBankDTO() {
        super();
    }

    public ConfirmLinkedBankDTO(String requestId, String otpValue, String applicationType, String bankCode,
            String bankAccount) {
        this.requestId = requestId;
        this.otpValue = otpValue;
        this.applicationType = applicationType;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOtpValue() {
        return otpValue;
    }

    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public String getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(String confirmId) {
        this.confirmId = confirmId;
    }

    public String getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        this.otpNumber = otpNumber;
    }
}
