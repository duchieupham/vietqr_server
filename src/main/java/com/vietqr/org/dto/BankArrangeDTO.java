package com.vietqr.org.dto;

public class BankArrangeDTO {
    private String bankId;
    private int index;

    public BankArrangeDTO() {
    }

    public BankArrangeDTO(String bankId, int index) {
        this.bankId = bankId;
        this.index = index;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
