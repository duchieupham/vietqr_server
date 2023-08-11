package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SystemSetting")
public class SystemSettingEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "androidVersion")
    private String androidVersion;

    @Column(name = "iosVersion")
    private String iosVersion;

    @Column(name = "telegramChatId")
    private String telegramChatId;

    @Column(name = "webhookUrl")
    private String webhookUrl;

    public SystemSettingEntity() {
        super();
    }

    public SystemSettingEntity(String id, String androidVersion, String iosVersion, String telegramChatId,
            String webhookUrl) {
        this.id = id;
        this.androidVersion = androidVersion;
        this.iosVersion = iosVersion;
        this.telegramChatId = telegramChatId;
        this.webhookUrl = webhookUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getIosVersion() {
        return iosVersion;
    }

    public void setIosVersion(String iosVersion) {
        this.iosVersion = iosVersion;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

}
