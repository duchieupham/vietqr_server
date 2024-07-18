package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalSyncDTO {
    private String terminalId;
    @NotBlank
    private String terminalName;
    private String terminalCode;
    @NotBlank
    private String terminalAddress;
    @NotBlank
    private String bankAccount;
    @NotBlank
    private String bankCode;
    @NotBlank
    private String checkSum;

    public TerminalSyncDTO(String terminalId, String terminalName, String terminalAddress, String bankAccount, String bankCode, String checkSum) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.checkSum = checkSum;
    }

    public TerminalSyncDTO() {
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
}
