package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TelegramUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String telegramId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    // Constructor, getters, and setters
    public TelegramUpdateDTO() {
        super();
    }

    public TelegramUpdateDTO(String telegramId, List<String> notificationTypes, List<String> notificationContents) {
        this.telegramId = telegramId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public List<String> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(List<String> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public List<String> getNotificationContents() {
        return notificationContents;
    }

    public void setNotificationContents(List<String> notificationContents) {
        this.notificationContents = notificationContents;
    }
}