package com.vietqr.org.dto;

public class FeeTransactionInfoDTO {
    private int  totalCount;
    private long  totalAmount;
    private int  creditCount;
    private long  creditAmount;
    private int debitCount;
    private long debitAmount;
    private int controlCount;
    private long controlAmount;
    private String bankId;

    public FeeTransactionInfoDTO() {
    }

    public FeeTransactionInfoDTO(int totalCount, long totalAmount, int creditCount, long creditAmount, int debitCount,
                                 long debitAmount, int controlCount, long controlAmount, String bankId) {
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.creditCount = creditCount;
        this.creditAmount = creditAmount;
        this.debitCount = debitCount;
        this.debitAmount = debitAmount;
        this.controlCount = controlCount;
        this.controlAmount = controlAmount;
        this.bankId = bankId;
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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
