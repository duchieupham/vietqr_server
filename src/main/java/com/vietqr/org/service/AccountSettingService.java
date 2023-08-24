package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSettingEntity;

@Service
public interface AccountSettingService {

    public int insertAccountSetting(AccountSettingEntity entity);

    public AccountSettingEntity getAccountSettingEntity(String userId);

    public void updateGuideWebByUserId(int guideWeb, String userId);

    public void updateVoiceMobile(int value, String userId);

    public void updateVoiceMobileKiot(int value, String userId);

    public void updateVoiceWeb(int value, String userId);

    public void updateAccessLogin(long lastLogin, long accessCount, String userId);

    public Long getAccessCountByUserId(String userId);
}
