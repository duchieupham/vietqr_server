package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class LarkInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String webhook;
    private String userId;
    private List<String> bankIds;
    private List<String> notificationTypes;
    private List<String> notificationContents;
    public LarkInsertDTO() {
        super();
    }

    public LarkInsertDTO(String name, String webhook, String userId, List<String> bankIds, List<String> notificationTypes, List<String> notificationContents) {
        this.name = name;
        this.webhook = webhook;
        this.userId = userId;
        this.bankIds = bankIds;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
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
}
