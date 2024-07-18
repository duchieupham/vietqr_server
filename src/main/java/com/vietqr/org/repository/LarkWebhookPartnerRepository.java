package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.LarkWebhookPartnerEntity;

@Repository
public interface LarkWebhookPartnerRepository extends JpaRepository<LarkWebhookPartnerEntity, Long> {

    // get list
    @Query(value = "SELECT * FROM lark_webhook_partner ", nativeQuery = true)
    List<LarkWebhookPartnerEntity> getLarkWebhookPartners();

    // delete
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lark_webhook_partner WHERE id = :id", nativeQuery = true)
    void removeLarkWebhookPartnerById(@Param(value = "id") String id);

    // update data
    @Transactional
    @Modifying
    @Query(value = "UPDATE lark_webhook_partner "
            + "SET partner_name = :partnerName, webhook = :webhook, description = :description "
            + "WHERE id = :id ", nativeQuery = true)
    void updateLarkWebhookPartner(
            @Param(value = "partnerName") String partnerName,
            @Param(value = "webhook") String webhook,
            @Param(value = "description") String description,
            @Param(value = "id") String id);

    // update status
    @Transactional
    @Modifying
    @Query(value = "UPDATE lark_webhook_partner "
            + "SET active = :active "
            + "WHERE id = :id ", nativeQuery = true)
    void updateLarkWebhookPartnerStatus(
            @Param(value = "active") boolean active,
            @Param(value = "id") String id);
}
