package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.FcmTokenEntity;

@Service
public interface FcmTokenService {

	public int insertFcmToken(FcmTokenEntity entity);

	public List<FcmTokenEntity> getFcmTokensByUserId(String userId);

	public void deleteFcmToken(String token);
}
