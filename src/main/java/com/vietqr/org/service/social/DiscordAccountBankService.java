package com.vietqr.org.service.social;

import com.vietqr.org.dto.DiscordBankDTO;
import com.vietqr.org.entity.DiscordAccountBankEntity;

import java.util.List;

public interface DiscordAccountBankService {
    int insert(DiscordAccountBankEntity entity);
    String checkExistedBankId(String bankId, String discordId);
    void deleteByBankIdAndDiscordId(String bankId, String discordId);
    void deleteByDiscordId(String discordId);
    List<DiscordBankDTO> getDiscordAccountBanks(String discordId);
    List<String> getWebhooksByBankId(String bankId);
    void updateWebHookDiscord(String webhook, String discordId);
}