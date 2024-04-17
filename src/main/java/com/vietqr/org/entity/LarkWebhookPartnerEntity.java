package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LarkWebhookPartner")
public class LarkWebhookPartnerEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "webhook")
    private String webhook;

    @Column(name = "partnerName")
    private String partnerName;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private Boolean active;

    public LarkWebhookPartnerEntity() {
        super();
    }

    public LarkWebhookPartnerEntity(String id, String webhook, String partnerName, String description, Boolean active) {
        this.id = id;
        this.webhook = webhook;
        this.partnerName = partnerName;
        this.description = description;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
