package com.vietqr.org.dto;

public class TerminalTidResponseDTO {
    private String terminalId;
    private String terminalName;

    private String terminalCode;
    private String terminalAddress;
    private String bankCode;
    private String bankAccount;
    private String bankAccountName;
    private String qrCode;
    private String bankName;

    public TerminalTidResponseDTO(String terminalId, String terminalName, String terminalCode,
                                  String terminalAddress, String bankCode, String bankAccount,
                                  String bankAccountName, String qrCode, String bankName) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.terminalAddress = terminalAddress;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.qrCode = qrCode;
        this.bankName = bankName;
    }

    public TerminalTidResponseDTO() {
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

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
