package com.vietqr.org.dto;

public class TopTerminalDTO {
    private String terminalId;
    private String terminalCode;
    private String terminalAddress;
    private String terminalName;
    private long totalAmount;
    private String date;

    public TopTerminalDTO(String terminalId, String terminalCode, String terminalAddress,
                          String terminalName, long totalAmount, String date) {
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalAddress = terminalAddress;
        this.terminalName = terminalName;
        this.totalAmount = totalAmount;
        this.date = date;
    }

    public TopTerminalDTO() {
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
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

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
