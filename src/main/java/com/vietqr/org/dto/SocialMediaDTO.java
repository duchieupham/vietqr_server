package com.vietqr.org.dto;

public class SocialMediaDTO {
    private String platform;
    private String chatId;
    private int accountConnected;

    public SocialMediaDTO() {
    }

    public SocialMediaDTO(String platform, String chatId, int accountConnected) {
        this.platform = platform;
        this.chatId = chatId;
        this.accountConnected = accountConnected;
    }

    public int getAccountConnected() {
        return accountConnected;
    }

    public void setAccountConnected(int accountConnected) {
        this.accountConnected = accountConnected;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
