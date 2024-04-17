package com.vietqr.org.dto;

import java.io.Serializable;

public class LarkWebhookPartnerUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String partnerName;
    private String webhook;
    private String description;
    private String id;

    public LarkWebhookPartnerUpdateDTO() {
        super();
    }

    public LarkWebhookPartnerUpdateDTO(String partnerName, String webhook, String description, String id) {
        this.partnerName = partnerName;
        this.webhook = webhook;
        this.description = description;
        this.id = id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
