package com.vietqr.org.dto;

public class TerminalSyncInterDTO {
    private String boxAddress;
    private String qrCertificate;
    private String bankCode;
    private String bankAccount;
    private String terminalName;
    public TerminalSyncInterDTO() {
    }

    public TerminalSyncInterDTO(String boxAddress, String bankCode,
                                String bankAccount) {
        this.boxAddress = boxAddress;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
    }

    public String getBoxAddress() {
        return boxAddress;
    }

    public void setBoxAddress(String boxAddress) {
        this.boxAddress = boxAddress;
    }

    public String getQrCertificate() {
        return qrCertificate;
    }

    public void setQrCertificate(String qrCertificate) {
        this.qrCertificate = qrCertificate;
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

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
}
