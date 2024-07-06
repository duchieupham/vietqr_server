package com.vietqr.org.dto;

import java.io.Serializable;

public class GoogleChatUpdateWebhookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;

    public GoogleChatUpdateWebhookDTO() {
        super();
    }

    public GoogleChatUpdateWebhookDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
