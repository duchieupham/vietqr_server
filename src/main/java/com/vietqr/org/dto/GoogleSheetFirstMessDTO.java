package com.vietqr.org.dto;

import java.io.Serializable;

public class GoogleSheetFirstMessDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;
    private String message;

    public GoogleSheetFirstMessDTO() {
        super();
    }

    public GoogleSheetFirstMessDTO(String webhook, String message) {
        this.webhook = webhook;
        this.message = message;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
