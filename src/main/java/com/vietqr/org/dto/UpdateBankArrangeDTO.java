package com.vietqr.org.dto;

import java.util.List;

public class UpdateBankArrangeDTO {
    private List<BankArrangeDTO> bankArranges;
    private String userId;

    public UpdateBankArrangeDTO() {
    }

    public UpdateBankArrangeDTO(List<BankArrangeDTO> bankArranges) {
        this.bankArranges = bankArranges;
    }

    public List<BankArrangeDTO> getBankArranges() {
        return bankArranges;
    }

    public void setBankArranges(List<BankArrangeDTO> bankArranges) {
        this.bankArranges = bankArranges;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
