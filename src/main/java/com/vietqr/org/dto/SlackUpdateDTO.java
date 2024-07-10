package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class SlackUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String slackId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public SlackUpdateDTO() {
        super();
    }

    public SlackUpdateDTO(String slackId, List<String> notificationTypes, List<String> notificationContents) {
        this.slackId = slackId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
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