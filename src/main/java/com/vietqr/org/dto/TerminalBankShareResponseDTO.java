package com.vietqr.org.dto;

import java.util.List;

public class TerminalBankShareResponseDTO {

    private String userId;

    private int totalBankShares;

    private List<BankShareResponseDTO> bankShares;

    public TerminalBankShareResponseDTO() {
    }

    public TerminalBankShareResponseDTO(String userId, int totalBankShares, List<BankShareResponseDTO> bankShares) {
        this.userId = userId;
        this.totalBankShares = totalBankShares;
        this.bankShares = bankShares;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalBankShares() {
        return totalBankShares;
    }

    public void setTotalBankShares(int totalBankShares) {
        this.totalBankShares = totalBankShares;
    }

    public List<BankShareResponseDTO> getBankShares() {
        return bankShares;
    }

    public void setBankShares(List<BankShareResponseDTO> bankShares) {
        this.bankShares = bankShares;
    }
}
