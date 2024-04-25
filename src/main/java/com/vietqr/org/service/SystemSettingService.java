package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.SystemSettingEntity;

@Service
public interface SystemSettingService {

    public SystemSettingEntity getSystemSetting();

    Double getVatSystemSetting();
}
