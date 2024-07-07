package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GoogleChat")
public class GoogleChatEntity implements Serializable {
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

    public GoogleChatEntity() {
        super();
    }

    public GoogleChatEntity(String id, String webhook, String userId, String notificationTypes, String notificationContents) {
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
