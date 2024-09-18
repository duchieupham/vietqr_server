package com.vietqr.org.dto;

public class AccountBankDTO {
    private String bankId;
    private String notificationTypes;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(String notificationTypes) {
        this.notificationTypes = notificationTypes;
    }
}
