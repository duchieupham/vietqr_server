package com.vietqr.org.dto;

public class TransStatisticResponseWebDTO {
    private int totalTrans;
    private long totalCashIn;
    private int totalSettled;
    private long totalCashSettled;
    private int totalUnsettled;
    private long totalCashUnsettled;
    private int totalTransRefund;
    private long totalCashRefund;

    public TransStatisticResponseWebDTO(int totalTrans, long totalCashIn,
                                        int totalSettled, long totalCashSettled,
                                        int totalUnsettled, long totalCashUnsettled) {
        this.totalTrans = totalTrans;
        this.totalCashIn = totalCashIn;
        this.totalSettled = totalSettled;
        this.totalCashSettled = totalCashSettled;
        this.totalUnsettled = totalUnsettled;
        this.totalCashUnsettled = totalCashUnsettled;
    }

    public TransStatisticResponseWebDTO() {
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(long totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public int getTotalSettled() {
        return totalSettled;
    }

    public void setTotalSettled(int totalSettled) {
        this.totalSettled = totalSettled;
    }

    public long getTotalCashSettled() {
        return totalCashSettled;
    }

    public void setTotalCashSettled(long totalCashSettled) {
        this.totalCashSettled = totalCashSettled;
    }

    public int getTotalUnsettled() {
        return totalUnsettled;
    }

    public void setTotalUnsettled(int totalUnsettled) {
        this.totalUnsettled = totalUnsettled;
    }

    public long getTotalCashUnsettled() {
        return totalCashUnsettled;
    }

    public void setTotalCashUnsettled(long totalCashUnsettled) {
        this.totalCashUnsettled = totalCashUnsettled;
    }

    public int getTotalTransRefund() {
        return totalTransRefund;
    }

    public void setTotalTransRefund(int totalTransRefund) {
        this.totalTransRefund = totalTransRefund;
    }

    public long getTotalCashRefund() {
        return totalCashRefund;
    }

    public void setTotalCashRefund(long totalCashRefund) {
        this.totalCashRefund = totalCashRefund;
    }
}
