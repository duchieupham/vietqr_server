package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.SystemSettingEntity;
import com.vietqr.org.repository.SystemSettingRepository;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Autowired
    SystemSettingRepository repo;

    @Override
    public SystemSettingEntity getSystemSetting() {
        return repo.getSystemSetting();
    }

}
