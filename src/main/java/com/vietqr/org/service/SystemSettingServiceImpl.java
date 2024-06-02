package com.vietqr.org.service;

import com.vietqr.org.dto.BoxEnvironmentResDTO;
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

    @Override
    public Double getVatSystemSetting() {
        return repo.getVatSystemSetting();
    }

    @Override
    public BoxEnvironmentResDTO getSystemSettingBoxEnv() {
        return repo.getSystemSettingBoxEnv();
    }

    @Override
    public int updateBoxEnvironment(String data) {
        return repo.updateBoxEnvironment(data);
    }

    @Override
    public String getBankIdRechargeDefault() {
        return repo.getBankIdRechargeDefault();
    }

    @Override
    public void updateBankRecharge(String bankId) {
        repo.updateBankRecharge(bankId);
    }

}
