package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class GoogleChatFirstMessDTO {
    @NotBlank
    private String webhook;

    public GoogleChatFirstMessDTO() {
    }

    public GoogleChatFirstMessDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
