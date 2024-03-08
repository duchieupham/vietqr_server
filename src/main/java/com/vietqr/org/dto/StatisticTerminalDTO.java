package com.vietqr.org.dto;

public class StatisticTerminalDTO {
    private int countTrans;
    private long sumAmount;
    private String time;

    public StatisticTerminalDTO(int countTrans, long sumAmount, String time) {
        this.countTrans = countTrans;
        this.sumAmount = sumAmount;
        this.time = time;
    }

    public StatisticTerminalDTO() {
    }

    public int getCountTrans() {
        return countTrans;
    }

    public void setCountTrans(int countTrans) {
        this.countTrans = countTrans;
    }

    public long getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(long sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
