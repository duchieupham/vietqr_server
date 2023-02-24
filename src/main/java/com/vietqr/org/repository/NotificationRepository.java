package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>{

	@Query(value = "SELECT * FROM notification WHERE user_id = :userId", nativeQuery = true)
	List<NotificationEntity> getNotificationsByUserId(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE notification SET is_read = true WHERE id = :id", nativeQuery = true)
	void updateNotificationStatus(@Param(value = "id")String id);

}
