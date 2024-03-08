package com.vietqr.org.dto;

public class TopTerminalDTO {
    private String terminalName;
    private long sumAmount;
    private String date;

    public TopTerminalDTO(String terminalName, long sumAmount, String date) {
        this.terminalName = terminalName;
        this.sumAmount = sumAmount;
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

    public long getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(long sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
