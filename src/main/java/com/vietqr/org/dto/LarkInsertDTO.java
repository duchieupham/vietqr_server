package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class LarkInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String webhook;
    private String userId;
    private List<String> bankIds;

    public LarkInsertDTO() {
        super();
    }

    public LarkInsertDTO(String webhook, String userId, List<String> bankIds) {
        this.webhook = webhook;
        this.userId = userId;
        this.bankIds = bankIds;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

}
