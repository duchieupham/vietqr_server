package com.vietqr.org.dto;

public class InvoiceUnpaidUserStaDTO {
    private String userId;
    private int totalInvoice;
    private long totalUnpaid;

    public InvoiceUnpaidUserStaDTO() {
    }

    public InvoiceUnpaidUserStaDTO(String userId, int totalInvoice, long totalUnpaid) {
        this.userId = userId;
        this.totalInvoice = totalInvoice;
        this.totalUnpaid = totalUnpaid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(int totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public long getTotalUnpaid() {
        return totalUnpaid;
    }

    public void setTotalUnpaid(long totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }
}