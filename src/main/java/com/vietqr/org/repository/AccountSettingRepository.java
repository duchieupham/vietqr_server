package com.vietqr.org.repository;

import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountSettingEntity;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AccountSettingRepository extends JpaRepository<AccountSettingEntity, Long> {

    @Query(value = "SELECT * FROM account_setting WHERE user_id = :userId", nativeQuery = true)
    AccountSettingEntity getAccountSettingByUserId(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET guide_web = :guideWeb WHERE user_id = :userId", nativeQuery = true)
    void updateGuideWebByUserId(@Param(value = "guideWeb") int guideWeb, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET voice_mobile = :value WHERE user_id = :userId", nativeQuery = true)
    void updateVoiceMobile(@Param(value = "value") int value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET voice_mobile_kiot = :value WHERE user_id = :userId", nativeQuery = true)
    void updateVoiceMobileKiot(@Param(value = "value") int value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET voice_web = :value WHERE user_id = :userId", nativeQuery = true)
    void updateVoiceWeb(@Param(value = "value") int value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET last_login = :lastLogin, access_count = :accessCount WHERE user_id = :userId", nativeQuery = true)
    void updateAccessLogin(@Param(value = "lastLogin") long lastLogin,
            @Param(value = "accessCount") long accessCount,
            @Param(value = "userId") String userId);

    @Query(value = "SELECT access_count FROM account_setting WHERE user_id = :userId", nativeQuery = true)
    Long getAccessCountByUserId(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET edge_img_id = :imgId WHERE user_id = :userId", nativeQuery = true)
    void updateEdgeImgId(@Param(value = "imgId") String imgId, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET footer_img_id = :imgId WHERE user_id = :userId", nativeQuery = true)
    void updateFooterImgId(@Param(value = "imgId") String imgId, @Param(value = "userId") String userId);

    //
    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET theme_type = :value WHERE user_id = :userId", nativeQuery = true)
    void updateThemeType(@Param(value = "value") int value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET keep_screen_on = :value WHERE user_id = :userId", nativeQuery = true)
    void updateKeepScreenOn(@Param(value = "value") boolean value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET qr_show_type = :value WHERE user_id = :userId", nativeQuery = true)
    void updateQrShowType(@Param(value = "value") int value, @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_setting SET notification_mobile = :value WHERE user_id = :userId", nativeQuery = true)
    void updateNotificationMobile(@Param(value = "value") boolean value, @Param(value = "userId") String userId);
}
