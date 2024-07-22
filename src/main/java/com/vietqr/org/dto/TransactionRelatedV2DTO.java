package com.vietqr.org.dto;

public interface TransactionRelatedV2DTO {
    String getTransactionId();

    String getAmount();

    String getQrCode();

    Integer getTime();

    Integer getTimePaid();

    Integer getStatus();

    Integer getType();

    String getTransType();

    String getReferenceNumber();

    String getOrderId();

    String getBankAccount();

    String getContent();
}
