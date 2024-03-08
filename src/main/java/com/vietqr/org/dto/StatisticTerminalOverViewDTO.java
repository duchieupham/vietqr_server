package com.vietqr.org.dto;

public class StatisticTerminalOverViewDTO {
    private String terminalId;
    private String terminalCode;
    private String terminalName;
    private String terminalAddress;
    private int totalTrans;
    private long totalAmount;
    private int ratePreviousDate;

    public StatisticTerminalOverViewDTO() {
    }

    public StatisticTerminalOverViewDTO(String terminalId, String terminalCode,
                                        String terminalName, String terminalAddress,
                                        int totalTrans, long totalAmount, int ratePreviousDate) {
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.ratePreviousDate = ratePreviousDate;
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

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getRatePreviousDate() {
        return ratePreviousDate;
    }

    public void setRatePreviousDate(int ratePreviousDate) {
        this.ratePreviousDate = ratePreviousDate;
    }
}
