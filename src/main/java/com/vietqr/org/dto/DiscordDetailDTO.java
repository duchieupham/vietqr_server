package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class DiscordDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String webhook;
    private String userId;
    private List<DiscordBankDTO> banks;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public DiscordDetailDTO() {
        super();
    }

    public DiscordDetailDTO(String id, String webhook, String userId, List<DiscordBankDTO> banks, List<String> notificationTypes, List<String> notificationContents) {
        this.id = id;
        this.webhook = webhook;
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

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<DiscordBankDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<DiscordBankDTO> banks) {
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