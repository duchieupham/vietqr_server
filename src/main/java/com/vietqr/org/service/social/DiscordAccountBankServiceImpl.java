package com.vietqr.org.service.social;


import com.vietqr.org.dto.DiscordBankDTO;
import com.vietqr.org.entity.DiscordAccountBankEntity;
import com.vietqr.org.repository.DiscordAccountBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordAccountBankServiceImpl implements DiscordAccountBankService {
    @Autowired
    DiscordAccountBankRepository repo;

    @Override
    public int insert(DiscordAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedBankId(String bankId, String discordId) {
        return repo.checkExistedBankId(bankId, discordId);
    }

    @Override
    public void deleteByBankIdAndDiscordId(String bankId, String discordId) {
        repo.deleteByBankIdAndDiscordId(bankId, discordId);
    }

    @Override
    public void deleteByDiscordId(String discordId) {
        repo.deleteByDiscordId(discordId);
    }

    @Override
    public List<DiscordBankDTO> getDiscordAccountBanks(String discordId) {
        return repo.getDiscordAccountBanks(discordId);
    }

    @Override
    public List<String> getWebhooksByBankId(String bankId) {
        return repo.getWebhooksByBankId(bankId);
    }

    @Override
    public void updateWebHookDiscord(String webhook, String discordId) {
        repo.updateWebHookDiscord(webhook, discordId);
    }

}