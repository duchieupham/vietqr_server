package com.vietqr.org.dto;

import java.util.Optional;

public interface AccountBankReceiveShareDTO {

    String getBankId();

    String getBankAccount();

    String getUserBankName();

    String getImgId();

    Integer getBankType();

    String getNationalId();

    String getPhoneAuthenticated();

    boolean getAuthenticated();

    String getUserId();

    boolean getIsOwner();

    String getVaNumber();

    String getBankTypeId();

    Boolean getIsValidService();

    Boolean getMmsActive();

    Long getValidFeeFrom();

    Long getValidFeeTo();

    int getPushNotification();

    int getEnableSoundNotification();
}
