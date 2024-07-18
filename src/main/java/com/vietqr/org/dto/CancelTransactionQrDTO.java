package com.vietqr.org.dto;

public class CancelTransactionQrDTO {
    private String transactionReceiveId;

    public CancelTransactionQrDTO() {
    }

    public CancelTransactionQrDTO(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }
}
