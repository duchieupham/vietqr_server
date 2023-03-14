package com.vietqr.org.dto;

public interface TransactionRelatedDTO {
    String getTransactionId();

    String getAmount();

    String getBankAccount();

    String getContent();

    Integer getTime();

    Integer getStatus();
}
