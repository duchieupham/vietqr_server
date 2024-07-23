package com.vietqr.org.dto;

import javax.validation.constraints.NotNull;

public class TransactionHashTagDTO {
    @NotNull
    private String hashTag;
    @NotNull
    private String transactionId;

    public TransactionHashTagDTO() {
    }

    public TransactionHashTagDTO(String hashTag, String transactionId) {
        this.hashTag = hashTag;
        this.transactionId = transactionId;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
