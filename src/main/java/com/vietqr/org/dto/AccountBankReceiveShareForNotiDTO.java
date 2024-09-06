package com.vietqr.org.dto;

public interface AccountBankReceiveShareForNotiDTO {
    String getBankId();

    String getBankAccount();

    String getBankName();

    String getBankCode();

    String getUserId();

    String getBankShortName();
    Boolean getIsValidService();

    int getPushNotification();
}
