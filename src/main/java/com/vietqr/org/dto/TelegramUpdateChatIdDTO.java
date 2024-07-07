package com.vietqr.org.dto;

public class TelegramUpdateChatIdDTO {
    private static final long serialVersionUID = 1L;

    private String chatId;

    public TelegramUpdateChatIdDTO() {
        super();
    }

    public TelegramUpdateChatIdDTO(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isValid() {
        return this.chatId != null;
    }
}
