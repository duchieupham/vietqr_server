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
}
