package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TelegramInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String chatId;
    private String userId;
    private String name;
    private List<String> bankIds;
    private List<String> notificationTypes; // Thêm trường này
    private List<String> notificationContents;
    public TelegramInsertDTO() {
        super();
    }

    public TelegramInsertDTO(String chatId, String userId, String name, List<String> bankIds) {
        this.chatId = chatId;
        this.userId = userId;
        this.name = name;
        this.bankIds = bankIds;
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


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

}
