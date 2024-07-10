package com.vietqr.org.service.social;

import com.vietqr.org.dto.SlackInfoDetailDTO;
import com.vietqr.org.entity.SlackEntity;
import com.vietqr.org.repository.SlackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlackServiceImpl implements SlackService {

    @Autowired
    SlackRepository repo;

    @Override
    public void insert(SlackEntity entity) {
        repo.save(entity);
    }

    @Override
    public void updateSlack(String webhook, String slackId) {
        repo.updateWebHookSlack(webhook, slackId);
    }

    @Override
    public SlackEntity getSlackById(String id) {
        return repo.getSlackById(id);
    }

    @Override
    public SlackEntity getSlackByUserId(String userId) {
        return repo.getSlackByUserId(userId);
    }

    @Override
    public void updateSlack(SlackEntity slackEntity) {
        repo.save(slackEntity);
    }

    @Override
    public void removeSlack(String id) {
        repo.deleteById(id);
    }

    @Override
    public int countSlacksByUserId(String userId) {
        return repo.countSlacksByUserId(userId);
    }

    @Override
    public List<SlackInfoDetailDTO> getSlacksByUserIdWithPagination(String userId, int offset, int size) {
        return repo.getSlacksByUserIdWithPagination(userId, offset, size);
    }


    @Override
    public SlackEntity getSlackByWebhook(String webhook) {
        return repo.findByWebhook(webhook);
    }
}