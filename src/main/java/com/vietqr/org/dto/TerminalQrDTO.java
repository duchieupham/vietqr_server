package com.vietqr.org.dto;

public class TerminalQrDTO {
    private String terminalId;
    private String terminalCode;
    private String terminalName;
    private String terminalAddress;
    private String userId;
    private String userBankName;
    private String bankShortName;
    private String bankAccount;
    private String qrCode;

    public TerminalQrDTO() {
    }

    public TerminalQrDTO(String terminalId, String terminalCode, String terminalName, String terminalAddress, String userId, String userBankName, String bankShortName, String bankAccount, String qrCode) {
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
        this.userId = userId;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.bankAccount = bankAccount;
        this.qrCode = qrCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
