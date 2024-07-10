package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class DiscordUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String discordId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public DiscordUpdateDTO() {
        super();
    }

    public DiscordUpdateDTO(String discordId, List<String> notificationTypes, List<String> notificationContents) {
        this.discordId = discordId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
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