package com.vietqr.org.dto;

import java.io.Serializable;

public class SlackFirstMessDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;

    public SlackFirstMessDTO() {
        super();
    }

    public SlackFirstMessDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}