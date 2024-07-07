package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TelegramDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String chatId;
    private String userId;
    private List<TelBankDTO> banks;
    private List<String> notificationTypes;
    private List<String> notificationContents;
    public TelegramDetailDTO() {
        super();
    }

    public TelegramDetailDTO(String id, String chatId, String userId, List<TelBankDTO> banks,
                             List<String> notificationTypes, List<String> notificationContents) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.banks = banks;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<TelBankDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<TelBankDTO> banks) {
        this.banks = banks;
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
