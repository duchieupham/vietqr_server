package com.vietqr.org.dto;

public interface TransactionCheckDTO {
    String getTransactionId();

    String getReferenceNumber();

    String getBankAccount();

    String getAmount();

    String getTransType();

    String getContent();

    String getTimeReceived();
}
