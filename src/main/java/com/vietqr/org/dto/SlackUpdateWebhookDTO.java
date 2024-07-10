package com.vietqr.org.dto;

import java.io.Serializable;

public class SlackUpdateWebhookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;

    public SlackUpdateWebhookDTO() {
        super();
    }

    public SlackUpdateWebhookDTO(String webhook) {
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