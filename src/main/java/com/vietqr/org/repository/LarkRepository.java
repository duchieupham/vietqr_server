package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.LarkEntity;

@Repository
public interface LarkRepository extends JpaRepository<LarkEntity, Long> {

    @Query(value = "SELECT * FROM lark WHERE user_id = :userId", nativeQuery = true)
    List<LarkEntity> getLarksByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM lark WHERE user_id = :userId", nativeQuery = true)
    LarkEntity getLarkByUserId(@Param(value = "userId") String userId);
    @Query(value = "SELECT * FROM lark WHERE id = :id", nativeQuery = true)
    LarkEntity getLarkById(@Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lark WHERE id = :id", nativeQuery = true)
    void removeLarkById(@Param(value = "id") String id);

    @Query(value="SELECT * FROM lark WHERE webhook = :webhook",nativeQuery = true)
    LarkEntity getLarkByWebhook(@Param("webhook") String webhook);

    @Transactional
    @Modifying
    @Query(value = "UPDATE lark SET webhook = :webhook WHERE id = :larkId", nativeQuery = true)
    void updateLark(String webhook, String larkId);
}
