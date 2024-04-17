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

}
