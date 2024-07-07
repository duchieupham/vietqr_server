package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.ISocialMediaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TelegramEntity;

@Repository
public interface TelegramRepository extends JpaRepository<TelegramEntity, Long> {

    @Query(value = "SELECT 'Telegram' AS platForm, a.chat_id AS chatId, " +
            "(SELECT COUNT(*) FROM telegram_account_bank t WHERE t.user_id = :userId) AS accountConnected " +
            "FROM telegram a " +
            "INNER JOIN telegram_account_bank b ON a.id = b.telegram_id " +
            "WHERE b.user_id = :userId " +
            "UNION ALL " +
            "SELECT 'Lark' AS platform, a.webhook AS chatId, " +
            "(SELECT COUNT(*) FROM lark_account_bank l WHERE l.user_id = :userId) AS accountConnected " +
            "FROM lark a " +
            "INNER JOIN lark_account_bank b ON b.lark_id = a.id " +
            "WHERE b.user_id = :userId " +
            "UNION ALL " +
            "SELECT 'Google Chat' AS platform, a.webhook AS chatId, " +
            "(SELECT COUNT(*) FROM google_chat_account_bank g WHERE g.user_id = :userId) AS accountConnected " +
            "FROM google_chat a " +
            "INNER JOIN google_chat_account_bank b ON b.google_chat_id = a.id " +
            "WHERE b.user_id = :userId ", nativeQuery = true)
    List<ISocialMediaDTO> getSocialInfoByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM telegram WHERE user_id = :userId", nativeQuery = true)
    List<TelegramEntity> getTelegramsByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM telegram WHERE id = :id", nativeQuery = true)
    TelegramEntity getTelegramById(@Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM telegram WHERE id = :id", nativeQuery = true)
    void removeTelegramById(@Param(value = "id") String id);

    @Query(value = "SELECT * FROM telegram WHERE chat_id = :chatId", nativeQuery = true)
    TelegramEntity getTelegramByChatId(@Param("chatId") String chatId);


    @Query(value = "SELECT * FROM telegram WHERE user_id = :userId", nativeQuery = true)
    TelegramEntity getTelegramByUserId(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE telegram SET chat_id = :chatId WHERE id = :id", nativeQuery = true)
    void updateTelegram(String chatId, String id);
}
