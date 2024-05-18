package com.vietqr.org.dto;

public class AdminExtraInvoiceDTO {
    private String month;
    private long pendingFee;
    private int pendingCount;
    private long completeFee;
    private int completeCount;

    public AdminExtraInvoiceDTO() {
    }

    public AdminExtraInvoiceDTO(String month, long pendingFee, int pendingCount, long completeFee, int completeCount) {
        this.month = month;
        this.pendingFee = pendingFee;
        this.pendingCount = pendingCount;
        this.completeFee = completeFee;
        this.completeCount = completeCount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getPendingFee() {
        return pendingFee;
    }

    public void setPendingFee(long pendingFee) {
        this.pendingFee = pendingFee;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getCompleteFee() {
        return completeFee;
    }

    public void setCompleteFee(long completeFee) {
        this.completeFee = completeFee;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }
}
