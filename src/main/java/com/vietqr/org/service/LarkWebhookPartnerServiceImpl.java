package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.LarkWebhookPartnerEntity;
import com.vietqr.org.repository.LarkWebhookPartnerRepository;

@Service
public class LarkWebhookPartnerServiceImpl implements LarkWebhookPartnerService {

    @Autowired
    LarkWebhookPartnerRepository repo;

    @Override
    public int insert(LarkWebhookPartnerEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<LarkWebhookPartnerEntity> getLarkWebhookPartners() {
        return repo.getLarkWebhookPartners();
    }

    @Override
    public void removeLarkWebhookPartnerById(String id) {
        repo.removeLarkWebhookPartnerById(id);
    }

    @Override
    public void updateLarkWebhookPartner(String partnerName, String webhook, String description, String id) {
        repo.updateLarkWebhookPartner(partnerName, webhook, description, id);
    }

    @Override
    public void updateLarkWebhookPartnerStatus(boolean active, String id) {
        repo.updateLarkWebhookPartnerStatus(active, id);
    }

}
