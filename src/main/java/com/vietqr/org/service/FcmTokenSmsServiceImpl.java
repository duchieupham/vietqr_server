package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.FcmTokenSmsEntity;
import com.vietqr.org.repository.FcmTokenSmsRepository;

@Service
public class FcmTokenSmsServiceImpl implements FcmTokenSmsService {

    @Autowired
    FcmTokenSmsRepository repo;

    @Override
    public int insert(FcmTokenSmsEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<FcmTokenEntity> getFcmTokenSmsBySmsId(String smsId) {
        return repo.getFcmTokenSmsBySmsId(smsId);
    }

    @Override
    public void deleteFcmTokenSms(String token) {
        repo.deleteFcmTokenSms(token);
    }

    @Override
    public void updateTokenSms(String newToken, String smsId, String oldToken) {
        repo.updateTokenSms(newToken, smsId, oldToken);
    }

    @Override
    public void deleteTokensBySmsId(String smsId) {
        repo.deleteTokensBySmsId(smsId);
    }

}
