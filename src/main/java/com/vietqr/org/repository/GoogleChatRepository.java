package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.GoogleChatEntity;

@Repository
public interface GoogleChatRepository extends JpaRepository<GoogleChatEntity, Long> {

    @Query(value = "SELECT * FROM google_chat WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    GoogleChatEntity getGoogleChatsByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM google_chat WHERE id = :id", nativeQuery = true)
    GoogleChatEntity getGoogleChatById(@Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM google_chat WHERE id = :id", nativeQuery = true)
    void removeGoogleChat(@Param(value = "id") String id);


    @Query(value ="SELECT * FROM google_chat WHERE webhook = :webhook", nativeQuery = true)
    GoogleChatEntity getGoogleChatsByWebhook(@Param("webhook") String webhook);

    @Transactional
    @Modifying
    @Query(value = "UPDATE google_chat SET webhook = :webhook WHERE id = :id", nativeQuery = true)
    void updateGoogleChat(String webhook, String id);
}
