package com.vietqr.org.dto;

public interface ITransactionReceiveAdminInfoDTO {
    String getTransactionId();

    int getAmount();

    long getTimePaid();

    String getReferenceNumber();

    String getOrderId();

    String getContent();
}
