package com.vietqr.org.dto;

public class TerminalResponseV2DTO {
    private String terminalId;
    private String terminalName;
    private String terminalCode;
    private String rawTerminalCode;
    private String terminalAddress;
    private String qrCode;

    public TerminalResponseV2DTO() {
    }

    public TerminalResponseV2DTO(String terminalId, String terminalName, String terminalCode,
                                 String rawTerminalCode, String terminalAddress) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.rawTerminalCode = rawTerminalCode;
        this.terminalAddress = terminalAddress;
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

    public String getRawTerminalCode() {
        return rawTerminalCode;
    }

    public void setRawTerminalCode(String rawTerminalCode) {
        this.rawTerminalCode = rawTerminalCode;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
