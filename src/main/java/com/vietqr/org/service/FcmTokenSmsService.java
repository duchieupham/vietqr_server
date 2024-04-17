package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.FcmTokenSmsEntity;

@Service
public interface FcmTokenSmsService {

    public int insert(FcmTokenSmsEntity entity);

    public List<FcmTokenEntity> getFcmTokenSmsBySmsId(String smsId);

    public void deleteFcmTokenSms(String token);

    public void updateTokenSms(String newToken, String smsId, String oldToken);

    public void deleteTokensBySmsId(String smsId);
}
