package com.vietqr.org.dto;

public interface IBankDetailAdminDTO {
    String getBankId();
    String getMerchantId();
    String getUserBankName();
    String getBankAccount();
    String getBankShortName();
    String getPhoneNo();
    String getEmail();
    Boolean getMmsActive();
    String getFeePackage();
    Double getVat();
    Integer getTransFee1();
    Double getTransFee2();
    Integer getTransRecord();
}
