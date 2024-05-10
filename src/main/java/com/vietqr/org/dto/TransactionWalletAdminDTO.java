package com.vietqr.org.dto;

public interface TransactionWalletAdminDTO {
    String getId();

    long getTimePaid();

    long getAmount();

    String getBillNumber();

    int getStatus();

    long getTimeCreated();

    String getFullName();

    String getPhoneNo();

    String getBankAccount();

    String getBankShortName();

    long getValidFeeFrom();

    long getValidFeeTo();
}
