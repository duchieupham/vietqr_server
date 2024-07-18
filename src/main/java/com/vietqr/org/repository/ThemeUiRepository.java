package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.ThemeUiEntity;

@Repository
public interface ThemeUiRepository extends JpaRepository<ThemeUiEntity, Long> {

    @Query(value = "SELECT * FROM theme_ui ", nativeQuery = true)
    List<ThemeUiEntity> getThemes();

    @Query(value = "SELECT img_url FROM theme_ui WHERE type = :value ", nativeQuery = true)
    String getImgUrlByType(@Param(value = "value") int value);
}
