package com.vietqr.org.dto;

public interface TransReceiveInvoicesDTO {
    String getId();
    long getAmount();
    String getContent();
    String getTransType();
    int getType();
    long getTimeCreate();
    long getTimePaid();
}
