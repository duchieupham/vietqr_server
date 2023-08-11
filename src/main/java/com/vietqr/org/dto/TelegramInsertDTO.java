package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TelegramInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String chatId;
    private String userId;
    private List<String> bankIds;

    public TelegramInsertDTO() {
        super();
    }

    public TelegramInsertDTO(String chatId, String userId, List<String> bankIds) {
        this.chatId = chatId;
        this.userId = userId;
        this.bankIds = bankIds;
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

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

}
