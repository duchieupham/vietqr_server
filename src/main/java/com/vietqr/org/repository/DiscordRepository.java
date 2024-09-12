package com.vietqr.org.repository;

import com.vietqr.org.dto.DiscordInfoDetailDTO;
import com.vietqr.org.entity.DiscordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DiscordRepository extends JpaRepository<DiscordEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE discord SET webhook = :webhook WHERE id = :discordId", nativeQuery = true)
    void updateWebhook(@Param("discordId") String discordId, @Param("webhook") String webhook);

    @Query(value = "SELECT COUNT(id) FROM discord WHERE user_id = :userId", nativeQuery = true)
    int countDiscordsByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM discord WHERE user_id = :userId", nativeQuery = true)
    DiscordEntity getDiscordByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM discord WHERE id = :id", nativeQuery = true)
    DiscordEntity getDiscordById(@Param("id") String id);

    @Query(value = "SELECT d.id AS discordId, d.webhook AS webhook, COALESCE(d.name, 'Chia sẻ biến động số dư') AS name, COUNT(dab.id) AS bankAccountCount " +
            "FROM discord d " +
            "LEFT JOIN discord_account_bank dab ON d.id = dab.discord_id " +
            "WHERE d.user_id = :userId " +
            "GROUP BY d.id, d.webhook, d.name " +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<DiscordInfoDetailDTO> getDiscordsByUserIdWithPagination(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT * FROM discord  WHERE webhook = :webhook",nativeQuery = true)
    DiscordEntity findByWebhook(@Param("webhook") String webhook);
}