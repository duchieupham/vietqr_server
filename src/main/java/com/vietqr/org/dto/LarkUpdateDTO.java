package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class LarkUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String larkId;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public LarkUpdateDTO() {
        super();
    }

    public LarkUpdateDTO(String larkId, List<String> notificationTypes, List<String> notificationContents) {
        this.larkId = larkId;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getLarkId() {
        return larkId;
    }

    public void setLarkId(String larkId) {
        this.larkId = larkId;
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
