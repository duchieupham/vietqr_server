package com.vietqr.org.dto;

public class CheckActiveKeyBank {
    private String keyActive;

    public CheckActiveKeyBank() {
    }

    public CheckActiveKeyBank(String keyActive) {
        this.keyActive = keyActive;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }
}
