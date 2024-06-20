package com.vietqr.org.dto;

public interface IListBankReceiveFeePackageDTO {
    String getId();
    String getTitle();
    Long getActiveFee();
    Long getAnnualFee();
    Long getFixFee();
    Double getPercentFee();
    Double getVat();
    Integer getRecordType();
    String getBankId();
    String getFeePackageId();
    String getMmsActive();
    String getBankAccount();
    String getUserBankName();
    String getBankShortName();
}
