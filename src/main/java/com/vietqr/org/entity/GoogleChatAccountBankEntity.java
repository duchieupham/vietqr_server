package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GoogleChatAccountBank")
public class GoogleChatAccountBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "googleChatId")
    private String googleChatId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "webhook")
    private String webhook;

    public GoogleChatAccountBankEntity() {
        super();
    }

    public GoogleChatAccountBankEntity(String id, String userId, String googleChatId, String bankId, String webhook) {
        this.id = id;
        this.userId = userId;
        this.googleChatId = googleChatId;
        this.bankId = bankId;
        this.webhook = webhook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoogleChatId() {
        return googleChatId;
    }

    public void setGoogleChatId(String googleChatId) {
        this.googleChatId = googleChatId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

}
