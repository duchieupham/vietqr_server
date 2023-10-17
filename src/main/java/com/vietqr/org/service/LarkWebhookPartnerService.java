package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.LarkWebhookPartnerEntity;

@Service
public interface LarkWebhookPartnerService {

    public int insert(LarkWebhookPartnerEntity entity);

    // get list
    public List<LarkWebhookPartnerEntity> getLarkWebhookPartners();

    // delete
    public void removeLarkWebhookPartnerById(String id);

    // update data
    public void updateLarkWebhookPartner(String partnerName, String webhook, String description, String id);

    // update status
    public void updateLarkWebhookPartnerStatus(boolean active, String id);
}
