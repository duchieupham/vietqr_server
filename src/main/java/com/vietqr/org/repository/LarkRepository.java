package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.LarkInfoDetailDTO;
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

    @Query(value = "SELECT l.id AS larkId, l.webhook AS webhook, COUNT(lab.id) AS bankAccountCount " +
            "FROM lark l " +
            "LEFT JOIN lark_account_bank lab ON l.id = lab.lark_id " +
            "WHERE l.user_id = :userId " +
            "GROUP BY l.id, l.webhook " +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<LarkInfoDetailDTO> getLarksByUserIdWithPagination(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT COUNT(l.id) FROM lark l WHERE l.user_id = :userId", nativeQuery = true)
    int countLarksByUserId(@Param("userId") String userId);

}
