package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class AdminConfirmActiveDTO {
    @NotBlank
    private String bankId;
    @NotBlank
    private String keyActive;
    @NotBlank
    private String checkSum;
    @NotBlank
    private String otp;

    public AdminConfirmActiveDTO() {
    }

    public AdminConfirmActiveDTO(String bankId, String keyActive, String checkSum, String otp) {
        this.bankId = bankId;
        this.keyActive = keyActive;
        this.checkSum = checkSum;
        this.otp = otp;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
