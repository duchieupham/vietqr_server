package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AccountSettingNotiDTO {
    @NotNull
    private boolean notificationMobile;
    @NotBlank
    private String userId;

    public AccountSettingNotiDTO() {
    }

    public AccountSettingNotiDTO(boolean notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public boolean isNotificationMobile() {
        return notificationMobile;
    }

    public void setNotificationMobile(boolean notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
