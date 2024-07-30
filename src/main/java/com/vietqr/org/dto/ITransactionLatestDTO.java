package com.vietqr.org.dto;

public interface ITransactionLatestDTO {
    String getAmount();
    String getTransType();
    Integer getStatus();
    Integer getType();
    Long getTime();
    Long getTimePaid();
    String getTransactionId();
}
