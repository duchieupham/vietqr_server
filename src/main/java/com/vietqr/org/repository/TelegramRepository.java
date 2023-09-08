package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TelegramEntity;

@Repository
public interface TelegramRepository extends JpaRepository<TelegramEntity, Long> {

    @Query(value = "SELECT * FROM telegram WHERE user_id = :userId", nativeQuery = true)
    List<TelegramEntity> getTelegramsByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM telegram WHERE id = :id", nativeQuery = true)
    TelegramEntity getTelegramById(@Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM telegram WHERE id = :id", nativeQuery = true)
    void removeTelegramById(@Param(value = "id") String id);
}
