package com.vietqr.org.dto;

public class TransReceiveInvoicesDTO {
    private String id;
    private long amount;
    private String content;
    private String transType;
    private int type;
    private long timeCreated;
    private long timePaid;
    private int status;

    public TransReceiveInvoicesDTO() {
    }

    public TransReceiveInvoicesDTO(String id, long amount, String content, String transType, int type, long timeCreated, long timePaid, int status) {
        this.id = id;
        this.amount = amount;
        this.content = content;
        this.transType = transType;
        this.type = type;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
