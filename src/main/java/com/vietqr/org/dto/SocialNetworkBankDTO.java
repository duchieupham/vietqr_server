package com.vietqr.org.dto;

import java.io.Serializable;

public class SocialNetworkBankDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String bankId;

    public SocialNetworkBankDTO() {
        super();
    }

    public SocialNetworkBankDTO(String id, String userId, String bankId) {
        this.id = id;
        this.userId = userId;
        this.bankId = bankId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

}
