package com.vietqr.org.dto;

public class FeeTransactionInfoDTOs {
    private String invoiceItemId;
    private String time;
    private int  totalCount;
    private long  totalAmount;
    private int  creditCount;
    private long  creditAmount;
    private int debitCount;
    private long debitAmount;
    private int controlCount;
    private long controlAmount;

    public FeeTransactionInfoDTOs() {
    }

    public FeeTransactionInfoDTOs(String invoiceItemId, String time, int totalCount, long totalAmount, int creditCount,
                                  long creditAmount, int debitCount, long debitAmount, int controlCount, long controlAmount) {
        this.invoiceItemId = invoiceItemId;
        this.time = time;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.creditCount = creditCount;
        this.creditAmount = creditAmount;
        this.debitCount = debitCount;
        this.debitAmount = debitAmount;
        this.controlCount = controlCount;
        this.controlAmount = controlAmount;
    }

    public String getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(String invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(int creditCount) {
        this.creditCount = creditCount;
    }

    public long getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(long creditAmount) {
        this.creditAmount = creditAmount;
    }

    public int getDebitCount() {
        return debitCount;
    }

    public void setDebitCount(int debitCount) {
        this.debitCount = debitCount;
    }

    public long getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(long debitAmount) {
        this.debitAmount = debitAmount;
    }

    public int getControlCount() {
        return controlCount;
    }

    public void setControlCount(int controlCount) {
        this.controlCount = controlCount;
    }

    public long getControlAmount() {
        return controlAmount;
    }

    public void setControlAmount(long controlAmount) {
        this.controlAmount = controlAmount;
    }
}