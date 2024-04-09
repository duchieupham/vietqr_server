package com.vietqr.org.dto;

public interface BankReceiveCheckDTO {
    String getBankId();
    String getUserId();
    boolean getAuthenticated();
    boolean getIsValidService();
    long getValidFrom();
    long getValidTo();
}
