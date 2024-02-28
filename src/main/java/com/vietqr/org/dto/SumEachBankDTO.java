package com.vietqr.org.dto;

public interface SumEachBankDTO {
    long getNumberOfBank();

    long getNumberOfBankAuthenticated();

    long getNumberOfBankNotAuthenticated();

    String getBankName();

    String getBankTypeId();

    String getBankShortName();

    String getImgId();

    String getBankCode();
}
