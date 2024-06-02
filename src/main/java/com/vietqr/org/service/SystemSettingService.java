package com.vietqr.org.service;

import com.vietqr.org.dto.BoxEnvironmentResDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.SystemSettingEntity;

@Service
public interface SystemSettingService {

    public SystemSettingEntity getSystemSetting();

    Double getVatSystemSetting();

    BoxEnvironmentResDTO getSystemSettingBoxEnv();

    int updateBoxEnvironment(String data);

    String getBankIdRechargeDefault();
}
