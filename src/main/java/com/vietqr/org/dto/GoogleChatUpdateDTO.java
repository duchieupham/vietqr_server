package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class GoogleChatUpdateDTO implements Serializable {
    private final static long serialVersionUID =1L;
    private String googleChatId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public GoogleChatUpdateDTO() {
        super();
    }

    public GoogleChatUpdateDTO(String googleChatId, List<String> notificationTypes, List<String> notificationContents) {
        this.googleChatId = googleChatId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getGoogleChatId() {
        return googleChatId;
    }

    public void setGoogleChatId(String googleChatId) {
        this.googleChatId = googleChatId;
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
