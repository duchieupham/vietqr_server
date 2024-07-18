package com.vietqr.org.dto;

public interface TransactionWalletAdminDTO {
    String getId();
    long getAmount();
    String getBillNumber();
    int getStatus();
    long getTimeCreated();
    long getTimePaid();
    String getTransType();
    int getPaymentType();
    String getPhoneNorc();
    String getUserId();
    String getFullName();
    String getPhoneNo();
    String getBankAccount();
    String getBankShortName();
}
