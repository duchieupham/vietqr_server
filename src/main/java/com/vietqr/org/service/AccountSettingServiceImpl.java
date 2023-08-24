package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSettingEntity;
import com.vietqr.org.repository.AccountSettingRepository;

@Service
public class AccountSettingServiceImpl implements AccountSettingService {

    @Autowired
    AccountSettingRepository repo;

    @Override
    public AccountSettingEntity getAccountSettingEntity(String userId) {
        return repo.getAccountSettingByUserId(userId);
    }

    @Override
    public void updateGuideWebByUserId(int guideWeb, String userId) {
        repo.updateGuideWebByUserId(guideWeb, userId);
    }

    @Override
    public int insertAccountSetting(AccountSettingEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updateVoiceMobile(int value, String userId) {
        repo.updateVoiceMobile(value, userId);
    }

    @Override
    public void updateVoiceMobileKiot(int value, String userId) {
        repo.updateVoiceMobileKiot(value, userId);
    }

    @Override
    public void updateVoiceWeb(int value, String userId) {
        repo.updateVoiceWeb(value, userId);
    }

    @Override
    public void updateAccessLogin(long lastLogin, long accessCount, String userId) {
        repo.updateAccessLogin(lastLogin, accessCount, userId);
    }

    @Override
    public Long getAccessCountByUserId(String userId) {
        return repo.getAccessCountByUserId(userId);
    }

}
