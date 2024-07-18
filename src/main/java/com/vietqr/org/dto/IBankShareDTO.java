package com.vietqr.org.dto;

public interface IBankShareDTO {
    String getBankAccount();
    String getBankAccountName();
    boolean getStatus();
    boolean getMmsActive();
    String getPhoneAuthenticated();
    String getNationalId();
    String getBankShortName();
    int getFromDate();
    int getToDate();
    int getActiveService();
}
