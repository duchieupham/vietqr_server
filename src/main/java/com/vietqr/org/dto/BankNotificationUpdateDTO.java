package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class BankNotificationUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String bankId;
    private List<String> notificationTypes;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public List<String> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(List<String> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }
}
