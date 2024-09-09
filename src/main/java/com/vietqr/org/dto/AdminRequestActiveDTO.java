package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class AdminRequestActiveDTO {
    @NotBlank
    private String bankId;

    @NotBlank
    private String checkSum;

    @NotBlank
    private String keyActive;

    public AdminRequestActiveDTO() {
    }

    public AdminRequestActiveDTO(String bankId, String checkSum, String keyActive) {
        this.bankId = bankId;
        this.checkSum = checkSum;
        this.keyActive = keyActive;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }
}
