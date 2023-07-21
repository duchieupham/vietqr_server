package com.vietqr.org.dto;

public interface TransactionCheckStatusDTO {
    String getAmount();

    String getBankAccount();

    String getBankName();

    Integer getTime();

    String getContent();

    String getReferenceNumber();

    String getTransType();

    Integer getStatus();
}
