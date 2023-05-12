package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.repository.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	NotificationRepository repo;

	@Override
	public int insertNotification(NotificationEntity entity) {
		return repo.save(entity) == null ? 0 : 1;
	}

	@Override
	public List<NotificationEntity> getNotificationsByUserId(String userId, int offset) {
		return repo.getNotificationsByUserId(userId, offset);
	}

	@Override
	public void updateNotificationStatus(String userId) {
		repo.updateNotificationStatus(userId);
	}

	@Override
	public int getNotificationCountByUserId(String userId) {
		return repo.getNotificationCountByUserId(userId);
	}

	@Override
	public void deleteNotificationsByUserId(String userId) {
		repo.deleteNotificationsByUserId(userId);
	}

}
