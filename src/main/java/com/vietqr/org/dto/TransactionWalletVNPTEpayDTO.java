package com.vietqr.org.dto;

public interface TransactionWalletVNPTEpayDTO {
    String getId();
    long getTimePaid();
    long getAmount();
    String getBillNumber();
    String getFullName();
    String getPhoneNo();
    String getPhoneNorc();
    String getEmail();
    long getTimeCreated();
    int getStatus();
}
