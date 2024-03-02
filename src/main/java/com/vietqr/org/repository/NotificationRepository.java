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
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

	@Query(value = "SELECT * FROM notification WHERE user_id = :userId "
			+ "AND time BETWEEN :fromDate AND :toDate "
			+ "ORDER BY time DESC LIMIT :offset, 20", nativeQuery = true)
	List<NotificationEntity> getNotificationsByUserId(@Param(value = "userId") String userId,
													  @Param(value = "offset") int offset,
													  @Param(value = "fromDate") long fromDate,
													  @Param(value = "toDate") long toDate);

	@Transactional
	@Modifying
	@Query(value = "UPDATE notification SET is_read = true WHERE user_id = :userId", nativeQuery = true)
	void updateNotificationStatus(@Param(value = "userId") String userId);

	@Query(value = "SELECT count(*) as count FROM notification WHERE is_read = 0 AND user_id = :userId "
			+ "time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
	int getNotificationCountByUserId(@Param(value = "userId") String userId,
									 @Param(value = "fromDate") long fromDate,
									 @Param(value = "toDate") long toDate);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM notification WHERE user_id = :userId", nativeQuery = true)
	void deleteNotificationsByUserId(@Param(value = "userId") String userId);
}
