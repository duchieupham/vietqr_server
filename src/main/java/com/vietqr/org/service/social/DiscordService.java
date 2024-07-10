package com.vietqr.org.service.social;

import com.vietqr.org.dto.DiscordInfoDetailDTO;
import com.vietqr.org.entity.DiscordEntity;

import java.util.List;

public interface DiscordService {
    void updateDiscord(String webhook, String discordId);
    int countDiscordsByUserId(String userId);
    List<DiscordInfoDetailDTO> getDiscordsByUserIdWithPagination(String userId, int offset, int size);
    DiscordEntity getDiscordByUserId(String userId);
    DiscordEntity getDiscordById(String id);
    void insert(DiscordEntity entity);
    void updateDiscord(DiscordEntity entity);
    void removeDiscord(String id);

    DiscordEntity getDiscordByWebhook(String webhook);
}
