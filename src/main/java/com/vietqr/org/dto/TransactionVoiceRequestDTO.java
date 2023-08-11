package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionVoiceRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String amount;
    // 0: mobile
    // 1: kiot
    // 2: web
    private int type;
    private String transactionId;

    //
    public TransactionVoiceRequestDTO() {
        super();
    }

    public TransactionVoiceRequestDTO(String userId, String amount, int type, String transactionId) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
