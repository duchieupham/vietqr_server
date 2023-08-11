package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TelegramAccountBank")
public class TelegramAccountBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "telegramId")
    private String telegramId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "chatId")
    private String chatId;

    public TelegramAccountBankEntity() {
        super();
    }

    public TelegramAccountBankEntity(String id, String userId, String telegramId, String bankId, String chatId) {
        this.id = id;
        this.userId = userId;
        this.telegramId = telegramId;
        this.bankId = bankId;
        this.chatId = chatId;
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

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

}
