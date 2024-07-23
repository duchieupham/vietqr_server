package com.vietqr.org.dto;

public class GenerateKeyBankBackUpDTO extends GenerateKeyBankDTO{
    private String bankId;
    private String userId;

    public GenerateKeyBankBackUpDTO() {
    }

    public GenerateKeyBankBackUpDTO(String bankId, String userId) {
        this.bankId = bankId;
        this.userId = userId;
    }

    public GenerateKeyBankBackUpDTO(int duration, int numOfKeys, String bankId, String userId) {
        super(duration, numOfKeys);
        this.bankId = bankId;
        this.userId = userId;
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


}
