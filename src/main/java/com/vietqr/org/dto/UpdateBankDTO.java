package com.vietqr.org.dto;

public class UpdateBankDTO {
    private String bankId;

    public UpdateBankDTO() {
    }

    public UpdateBankDTO(String bankId) {
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
