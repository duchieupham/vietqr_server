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

}
