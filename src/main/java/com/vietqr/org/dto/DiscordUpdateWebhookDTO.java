package com.vietqr.org.dto;

import java.io.Serializable;

public class DiscordUpdateWebhookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;

    public DiscordUpdateWebhookDTO() {
        super();
    }

    public DiscordUpdateWebhookDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public boolean isValid() {
        return webhook != null && !webhook.trim().isEmpty();
    }
}