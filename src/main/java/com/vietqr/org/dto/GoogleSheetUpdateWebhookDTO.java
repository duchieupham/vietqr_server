package com.vietqr.org.dto;

public class GoogleSheetUpdateWebhookDTO {

    private String webhook;

    public GoogleSheetUpdateWebhookDTO() {
        super();
    }

    public GoogleSheetUpdateWebhookDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
