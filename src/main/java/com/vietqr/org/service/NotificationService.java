package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.NotificationEntity;

@Service
public interface NotificationService {

	public int insertNotification(NotificationEntity entity);

	public List<NotificationEntity> getNotificationsByUserId(String userId);

	public void updateNotificationStatus(String id);
}
