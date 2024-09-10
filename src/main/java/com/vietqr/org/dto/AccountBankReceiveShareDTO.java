package com.vietqr.org.dto;

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

    Boolean getEnableVoice();

    Long getValidFeeFrom();

    Long getValidFeeTo();

    Integer getPushNotification();
}
