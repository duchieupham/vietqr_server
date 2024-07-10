package com.vietqr.org.service.social;

import com.vietqr.org.dto.DiscordInfoDetailDTO;
import com.vietqr.org.entity.DiscordEntity;
import com.vietqr.org.repository.DiscordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordServiceImpl implements DiscordService {

    @Autowired
    DiscordRepository repo;

    @Override
    public void updateDiscord(String webhook, String discordId) {
        repo.updateWebhook(discordId, webhook);
    }

    @Override
    public int countDiscordsByUserId(String userId) {
        return repo.countDiscordsByUserId(userId);
    }

    @Override
    public List<DiscordInfoDetailDTO> getDiscordsByUserIdWithPagination(String userId, int offset, int size) {
        return repo.getDiscordsByUserIdWithPagination(userId, offset, size);
    }

    @Override
    public DiscordEntity getDiscordByUserId(String userId) {
        return repo.getDiscordByUserId(userId);
    }

    @Override
    public DiscordEntity getDiscordById(String id) {
        return repo.getDiscordById(id);
    }

    @Override
    public void insert(DiscordEntity entity) {
        repo.save(entity);
    }

    @Override
    public void updateDiscord(DiscordEntity entity) {
        repo.save(entity);
    }

    @Override
    public void removeDiscord(String id) {
        repo.deleteById(id);
    }

    @Override
    public DiscordEntity getDiscordByWebhook(String webhook) {
        return repo.findByWebhook(webhook);
    }
}