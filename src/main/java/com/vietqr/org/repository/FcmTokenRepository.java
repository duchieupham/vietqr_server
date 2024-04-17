package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.FcmTokenEntity;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity, Long> {

	@Query(value = "SELECT * FROM fcm_token WHERE user_id = :userId", nativeQuery = true)
	List<FcmTokenEntity> getFcmTokensByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT * FROM fcm_token WHERE user_id = :userId AND platform IN ('ANDROID_KIOT', 'IOS_KIOT')", nativeQuery = true)
	List<FcmTokenEntity> getFcmTokensKiotByUserId(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM fcm_token WHERE token = :token", nativeQuery = true)
	void deleteFcmToken(@Param(value = "token") String token);

	@Transactional
	@Modifying
	@Query(value = "UPDATE fcm_token SET token = :newToken WHERE user_id = :userId AND token = :oldToken", nativeQuery = true)
	void updateToken(@Param(value = "newToken") String newToken, @Param(value = "userId") String userId,
			@Param(value = "oldToken") String oldToken);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM fcm_token WHERE user_id = :userId", nativeQuery = true)
	void deleteTokensByUserId(@Param(value = "userId") String userId);
}
