package com.vietqr.org.dto;

public interface IListAccountBankDTO {
    String getBankId();

    String getBankAccount();

    String getBankAccountName();

    String getBankTypeId();

    boolean getIsAuthenticated();

    int getType();

    boolean getIsSync();

    boolean getIsWpSync();

    boolean getMmsActive();

    String getNationalId();

    String getPhoneAuthenticated();

    boolean getStatus();

    String getUserId();

    boolean getIsRpaSync();
}
