package com.vietqr.org.dto;

import com.vietqr.org.json.AccountSettingConfigDTO;

public class UserConfigAccountSettingDTO {
    private String userId;
    private AccountSettingConfigDTO userConfig;

    public UserConfigAccountSettingDTO() {
    }

    public UserConfigAccountSettingDTO(String userId, AccountSettingConfigDTO userConfig) {
        this.userId = userId;
        this.userConfig = userConfig;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AccountSettingConfigDTO getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(AccountSettingConfigDTO userConfig) {
        this.userConfig = userConfig;
    }
}
