package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "GoogleSheet")
public class GoogleSheetEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "webhook")
    private String webhook;

    @Column(name = "userId")
    private String userId;

    @Column(name = "notificationTypes")
    private String notificationTypes;

    @Column(name = "notificationContents")
    private String notificationContents;

    public GoogleSheetEntity() {
        super();
    }

    public GoogleSheetEntity(String id, String webhook, String userId, String notificationTypes, String notificationContents) {
        this.id = id;
        this.webhook = webhook;
        this.userId = userId;
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


    public String getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(String notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public String getNotificationContents() {
        return notificationContents;
    }

    public void setNotificationContents(String notificationContents) {
        this.notificationContents = notificationContents;
    }
}
