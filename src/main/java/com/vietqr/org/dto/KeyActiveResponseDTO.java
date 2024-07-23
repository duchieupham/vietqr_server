package com.vietqr.org.dto;

public class KeyActiveResponseDTO {
    private String keyActive;
    private String bankId;
    private int status;

    public KeyActiveResponseDTO() {
    }

    public KeyActiveResponseDTO(String keyActive, String bankId, int status) {
        this.keyActive = keyActive;
        this.bankId = bankId;
        this.status = status;
    }


    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
