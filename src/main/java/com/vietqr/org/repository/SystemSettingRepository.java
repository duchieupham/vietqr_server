package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.SystemSettingEntity;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, Long> {

    @Query(value = "SELECT * FROM system_setting", nativeQuery = true)
    SystemSettingEntity getSystemSetting();
}
