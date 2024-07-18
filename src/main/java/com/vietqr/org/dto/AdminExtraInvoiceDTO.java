package com.vietqr.org.dto;

public class AdminExtraInvoiceDTO {
    private String month;
    private long pendingAmount;
    private int pendingCount;
    private long completeAmount;
    private int completeCount;
    private int unFullyPaidCount;

    public AdminExtraInvoiceDTO() {
        month = "";
        pendingAmount = 0;
        pendingCount = 0;
        completeAmount = 0;
        completeCount = 0;
        unFullyPaidCount = 0;
    }

    public AdminExtraInvoiceDTO(String month, long pendingAmount, int pendingCount, long completeAmount, int completeCount) {
        this.month = month;
        this.pendingAmount = pendingAmount;
        this.pendingCount = pendingCount;
        this.completeAmount = completeAmount;
        this.completeCount = completeCount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(long pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getCompleteAmount() {
        return completeAmount;
    }

    public void setCompleteAmount(long completeAmount) {
        this.completeAmount = completeAmount;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public int getUnFullyPaidCount() {
        return unFullyPaidCount;
    }

    public void setUnFullyPaidCount(int unFullyPaidCount) {
        this.unFullyPaidCount = unFullyPaidCount;
    }
}
