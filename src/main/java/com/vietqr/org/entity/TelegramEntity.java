package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Telegram")
public class TelegramEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "chatId")
    private String chatId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "notificationTypes")
    private String notificationTypes; // Lưu dưới dạng JSON

    @Column(name = "notificationContents")
    private String notificationContents; // Lưu nội dung thông báo dưới dạng JSON

    public TelegramEntity() {
        super();
    }

    public TelegramEntity(String id, String name, String chatId, String userId, String notificationTypes, String notificationContents) {
        this.id = id;
        this.name = name;
        this.chatId = chatId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
