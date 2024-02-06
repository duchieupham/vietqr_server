package com.vietqr.org.dto;

public interface TransactionTerminalRelatedDTO {
    String getTransactionId();

    String getAmount();

    String getBankAccount();

    String getContent();

    Integer getTime();

    Integer getTimePaid();

    Integer getStatus();

    Integer getType();

    String getTransType();

    String getTerminalCode();

    String getTerminalName();
}
