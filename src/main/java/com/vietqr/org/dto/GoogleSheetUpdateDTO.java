package com.vietqr.org.dto;

import java.util.List;

public class GoogleSheetUpdateDTO {
    private String googleSheetId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public GoogleSheetUpdateDTO() {
        super();
    }

    public GoogleSheetUpdateDTO(String googleSheetId, List<String> notificationTypes, List<String> notificationContents) {
        this.googleSheetId = googleSheetId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getGoogleSheetId() {
        return googleSheetId;
    }

    public void setGoogleSheetId(String googleSheetId) {
        this.googleSheetId = googleSheetId;
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
