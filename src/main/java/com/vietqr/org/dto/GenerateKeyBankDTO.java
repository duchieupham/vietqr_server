package com.vietqr.org.dto;

public class GenerateKeyBankDTO {
    private int duration;
    private int numOfKeys;

    public GenerateKeyBankDTO() {
    }

    public GenerateKeyBankDTO(int duration, int numOfKeys) {
        this.duration = duration;
        this.numOfKeys = numOfKeys;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNumOfKeys() {
        return numOfKeys;
    }

    public void setNumOfKeys(int numOfKeys) {
        this.numOfKeys = numOfKeys;
    }
}
