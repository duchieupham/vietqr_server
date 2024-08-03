package com.vietqr.org.dto;

public interface IBankAccountResponseDTO {
    String getBankId();
    String getBankAccount();
    String getBankAccountName();
    String getBankShortName();
    String getPhoneAuthenticated();
    boolean getMmsActive();
    String getNationalId();
    Long getValidFeeTo();
    String getPhoneNo();
    String getEmail();
    boolean getStatus();
    String getVso();
    Boolean getIsValidService();
    Boolean getIsAuthenticated();
    Integer getBankTypeStatus();
    String getBankCode();
}
