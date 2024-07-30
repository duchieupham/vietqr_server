package com.vietqr.org.dto;

public class TransactionLatestDTO {
    private String transactionId;
    private String amount;
    private String transType;
    private int status;
    private int type;
    private long time;
    private long timePaid;

    public TransactionLatestDTO() {
    }

    public TransactionLatestDTO(String transactionId, String amount, String transType, int status, int type, long time, long timePaid) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transType = transType;
        this.status = status;
        this.type = type;
        this.time = time;
        this.timePaid = timePaid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }
}
