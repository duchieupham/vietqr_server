package com.vietqr.org.dto;

public interface ITransactionRelatedDetailDTO {
    String getTransactionId();

    long getAmount();

    String getBankAccount();

    String getBankName();

    String getBankShortName();

    String getBankCode();

    String getContent();

    long getTime();

    long getTimePaid();

    String getTransType();

    int getStatus();

    int getType();

    String getNote();

    String getReferenceNumber();

    String getOrderId();

    String getTerminalCode();
}
