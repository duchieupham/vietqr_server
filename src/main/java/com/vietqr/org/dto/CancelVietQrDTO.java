package com.vietqr.org.dto;

public class CancelVietQrDTO {
    private String transactionReceiveId;

    public CancelVietQrDTO() {
    }

    public CancelVietQrDTO(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }
}
