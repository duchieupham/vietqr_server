package com.vietqr.org.dto;

public interface IBankNotificationProjection {
    String getId();
    String getBankAccount();
    String getBankAccountName();
    String getBankTypeId();
    Integer getIsAuthenticated();
    Integer getIsSync();
    Integer getIsWpSync();
    Integer getStatus();
    String getNationalId();
    String getPhoneAuthenticated();
    Integer getMmsActive();
    Integer getType();
    String getUserId();
    Integer getIsRpaSync();
    String getUsername();
    String getPassword();
    String getEwalletToken();
    Integer getTerminalLength();
    Integer getEnableVoice();
    Long getValidFeeFrom();
    Long getValidFeeTo();
    String getCustomerId();
    Long getTimeCreated();
    String getVso();
    Integer getPushNotification();
    Integer getValidService();
    String getNotificationTypes();
    String getBankShortName();
    String getImgId();
}
