package com.vietqr.org.dto;

import java.io.Serializable;

public class TransSyncRpaDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String time;
    private long amount;
    private String content;
    private String referenceNumber;
    private String transType;

    public TransSyncRpaDTO() {
        super();
    }

    public TransSyncRpaDTO(String id, String time, long amount, String content, String referenceNumber,
            String transType) {
        this.id = id;
        this.time = time;
        this.amount = amount;
        this.content = content;
        this.referenceNumber = referenceNumber;
        this.transType = transType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

}
