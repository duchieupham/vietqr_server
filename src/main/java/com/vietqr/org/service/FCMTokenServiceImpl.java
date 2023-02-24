package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.repository.FcmTokenRepository;

@Service
public class FCMTokenServiceImpl implements FcmTokenService {

	@Autowired
	FcmTokenRepository repo;

	@Override
	public int insertFcmToken(FcmTokenEntity entity) {
		return repo.save(entity) == null ? 0 : 1;
	}

	@Override
	public List<FcmTokenEntity> getFcmTokensByUserId(String userId) {
		return repo.getFcmTokensByUserId(userId);
	}

	@Override
	public void deleteFcmToken(String token) {
		repo.deleteFcmToken(token);
	}

}
