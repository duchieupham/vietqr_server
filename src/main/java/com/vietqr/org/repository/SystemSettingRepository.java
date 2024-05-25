package com.vietqr.org.repository;

import com.vietqr.org.dto.BoxEnvironmentResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.SystemSettingEntity;

import javax.transaction.Transactional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, Long> {

    @Query(value = "SELECT * FROM system_setting", nativeQuery = true)
    SystemSettingEntity getSystemSetting();

    @Query(value = "SELECT vat FROM system_setting LIMIT 1", nativeQuery = true)
    Double getVatSystemSetting();

    @Query(value = "SELECT box_env AS boxEnv FROM system_setting LIMIT 1", nativeQuery = true)
    BoxEnvironmentResDTO getSystemSettingBoxEnv();

    @Transactional
    @Modifying
    @Query(value = "UPDATE system_setting SET box_env = :data ", nativeQuery = true)
    int updateBoxEnvironment(String data);
}
