package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class SocialNetworkBanksDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private List<String> bankIds;

    public SocialNetworkBanksDTO() {
        super();
    }

    public SocialNetworkBanksDTO(String id, String userId, List<String> bankIds) {
        this.id = id;
        this.userId = userId;
        this.bankIds = bankIds;
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

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

}
