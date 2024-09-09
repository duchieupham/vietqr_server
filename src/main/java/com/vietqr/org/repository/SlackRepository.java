package com.vietqr.org.repository;

import com.vietqr.org.dto.SlackInfoDetailDTO;
import com.vietqr.org.entity.SlackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SlackRepository  extends JpaRepository<SlackEntity, String> {
    @Query(value = "SELECT * FROM slack WHERE id = :id LIMIT 1", nativeQuery = true)
    SlackEntity getSlackById(@Param(value = "id") String id);

    @Query(value = "SELECT * FROM slack WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    SlackEntity getSlackByUserId(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE slack SET webhook = :webhook WHERE id = :slackId", nativeQuery = true)
    void updateWebHookSlack(@Param("webhook") String webhook, @Param("slackId") String slackId);

    @Query(value = "SELECT COUNT(id) FROM slack WHERE user_id = :userId", nativeQuery = true)
    int countSlacksByUserId(@Param("userId") String userId);

    @Query(value = "SELECT s.id AS slackId, s.webhook AS webhook, s.name AS name, COUNT(sab.id) AS bankAccountCount " +
            "FROM slack s " +
            "LEFT JOIN slack_account_bank sab ON s.id = sab.slack_id " +
            "WHERE s.user_id = :userId " +
            "GROUP BY s.id, s.webhook, s.name " +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<SlackInfoDetailDTO> getSlacksByUserIdWithPagination(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT * FROM slack  WHERE webhook = :webhook", nativeQuery = true)
    SlackEntity findByWebhook(@Param("webhook") String webhook);
}
