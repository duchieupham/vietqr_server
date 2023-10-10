package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionSmsItemDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // id, amount, trans_type, time, timePaid, content
    private String id;
    private Long amount;
    private String transType;
    private Long time;
    private Long timePaid;
    private String content;

    public TransactionSmsItemDTO() {
        super();
    }

    public TransactionSmsItemDTO(String id, Long amount, String transType, Long time, Long timePaid, String content) {
        this.id = id;
        this.amount = amount;
        this.transType = transType;
        this.time = time;
        this.timePaid = timePaid;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Long timePaid) {
        this.timePaid = timePaid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
