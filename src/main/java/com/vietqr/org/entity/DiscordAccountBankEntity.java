package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DiscordAccountBank")
public class DiscordAccountBankEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "discordId")
    private String discordId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "webhook")
    private String webhook;

    public DiscordAccountBankEntity() {
        super();
    }

    public DiscordAccountBankEntity(String id, String userId, String discordId, String bankId, String webhook) {
        this.id = id;
        this.userId = userId;
        this.discordId = discordId;
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

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
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
