package com.vietqr.org.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class LarkUpdateWebhookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String webhook;

    public LarkUpdateWebhookDTO() {
        super();
    }

    public LarkUpdateWebhookDTO(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public boolean isValid() {
        return this.webhook != null;
    }

}
