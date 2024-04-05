package com.vietqr.org.dto;

public interface KeyBankReceiveActiveDTO {
    String getBankId();
    String getUserId();
    long getValidFeeFrom();
    long getValidFeeTo();
    boolean getIsValidService();
}
