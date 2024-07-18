package com.vietqr.org.dto;

public class ActiveQrBoxDTO {
    private String qrCertificate;
    private String terminalId;
    private String bankId;
    private String userId;

    public ActiveQrBoxDTO() {
    }

    public ActiveQrBoxDTO(String qrCertificate, String terminalId, String bankId, String userId) {
        this.qrCertificate = qrCertificate;
        this.terminalId = terminalId;
        this.bankId = bankId;
        this.userId = userId;
    }

    public String getQrCertificate() {
        return qrCertificate;
    }

    public void setQrCertificate(String qrCertificate) {
        this.qrCertificate = qrCertificate;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
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
