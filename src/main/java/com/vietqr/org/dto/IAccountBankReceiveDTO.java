package com.vietqr.org.dto;

public interface IAccountBankReceiveDTO {
    Boolean getIsAuthenticated();
    Boolean getIsMmsActive();
    String getBankAccount();
    String getBankId();
    String getUserBankName();
    String getBankShortName();
    String getBankCode();
}