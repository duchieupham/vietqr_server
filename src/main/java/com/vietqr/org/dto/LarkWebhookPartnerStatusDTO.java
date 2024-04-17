package com.vietqr.org.dto;

import java.io.Serializable;

public class LarkWebhookPartnerStatusDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private Boolean active;

    public LarkWebhookPartnerStatusDTO() {
        super();
    }

    public LarkWebhookPartnerStatusDTO(String id, Boolean active) {
        this.id = id;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
