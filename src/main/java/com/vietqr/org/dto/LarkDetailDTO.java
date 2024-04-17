package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class LarkDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String webhook;
    private String userId;
    private List<LarkBankDTO> banks;

    public LarkDetailDTO() {
        super();
    }

    public LarkDetailDTO(String id, String webhook, String userId, List<LarkBankDTO> banks) {
        this.id = id;
        this.webhook = webhook;
        this.userId = userId;
        this.banks = banks;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<LarkBankDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<LarkBankDTO> banks) {
        this.banks = banks;
    }

}
