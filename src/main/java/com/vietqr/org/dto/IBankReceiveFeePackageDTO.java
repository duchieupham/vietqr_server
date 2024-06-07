package com.vietqr.org.dto;

public interface IBankReceiveFeePackageDTO {
    String getBankAccount();
    String getBankShortName();
    String getUserBankName();
    Long getFixFee();
    Integer getRecordType();
    double getVat();
    double getPercentFee();
    String getTitle();
    boolean getMmsActive();
}
