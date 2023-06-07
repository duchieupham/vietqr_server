package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSettingUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private int guideWeb;

    public AccountSettingUpdateDTO() {
        super();
    }

    public AccountSettingUpdateDTO(String userId, int guideWeb) {
        this.userId = userId;
        this.guideWeb = guideWeb;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGuideWeb() {
        return guideWeb;
    }

    public void setGuideWeb(int guideWeb) {
        this.guideWeb = guideWeb;
    }

}
