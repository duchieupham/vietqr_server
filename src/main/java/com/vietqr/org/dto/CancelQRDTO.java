package com.vietqr.org.dto;

public class CancelQRDTO {
    private String notificationType;
    private String transactionReceiveId;

    public CancelQRDTO() {
    }

    public CancelQRDTO(String notificationType, String transactionReceiveId) {
        this.notificationType = notificationType;
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }
}
