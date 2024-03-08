package com.vietqr.org.dto;

public class StatisticTerminalDTO {
    private int totalTrans;
    private long totalAmount;
    private String timeDate;

    public StatisticTerminalDTO(int totalTrans, long totalAmount, String timeDate) {
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.timeDate = timeDate;
    }

    public StatisticTerminalDTO() {
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

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }
}
