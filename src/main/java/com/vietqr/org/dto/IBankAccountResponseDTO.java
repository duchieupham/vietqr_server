package com.vietqr.org.dto;

public interface IBankAccountResponseDTO {
    String getBankAccount();
    String getBankAccountName();
    String getBankShortName();
    String getPhoneAuthenticated();
    boolean getMmsActive();
    String getNationalId();
    Long getValidFeeFrom();
    String getPhoneNo();
    String getEmail();
    boolean getStatus();
}
