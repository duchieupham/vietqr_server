package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.util.DateTimeUtil;
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
	public List<NotificationEntity> getNotificationsByUserId(String userId, int offset, String fromDate, String toDate) {
		long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
		long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
		return repo.getNotificationsByUserId(userId, offset,
				fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
				toTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
	}

	@Override
	public List<NotificationEntity> getNotificationsByUserIdAWeek(String userId, int offset) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndWeek();
		return repo.getNotificationsByUserId(userId, offset,
				startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
				startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
	}

	@Override
	public void updateNotificationStatus(String userId) {
		repo.updateNotificationStatus(userId);
	}

	@Override
	public int getNotificationCountByUserId(String userId) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.get1MonthsPreviousAsLongInt();
		return repo.getNotificationCountByUserId(userId,
				startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
				startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
	}

	@Override
	public void deleteNotificationsByUserId(String userId) {
		repo.deleteNotificationsByUserId(userId);
	}

}
