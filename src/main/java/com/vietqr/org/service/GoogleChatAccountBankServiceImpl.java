package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.GoogleChatBankDTO;
import com.vietqr.org.entity.GoogleChatAccountBankEntity;
import com.vietqr.org.repository.GoogleChatAccountBankRepository;

@Service
public class GoogleChatAccountBankServiceImpl implements GoogleChatAccountBankService {

    @Autowired
    GoogleChatAccountBankRepository repo;

    @Override
    public int insert(GoogleChatAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedBankId(String bankId, String googleChatId) {
        return repo.checkExistedBankId(bankId, googleChatId);
    }

    @Override
    public void deleteByBankIdAndGoogleChatId(String bankId, String googleChatId) {
        repo.deleteByBankIdAndGoogleChatId(bankId, googleChatId);
    }

    @Override
    public void deleteByGoogleChatId(String googleChatId) {
        repo.deleteByGoogleChatId(googleChatId);
    }

    @Override
    public List<GoogleChatBankDTO> getGoogleAccountBanks(String googleChatId) {
        return repo.getGoogleAccountBanks(googleChatId);
    }

    @Override
    public List<String> getWebhooksByBankId(String bankId) {
        return repo.getWebhooksByBankId(bankId);
    }

    @Override
    public void updateWebHookGoogleChat(String webhook, String ggChatId) {
        repo.updateWebHookGoogleChat(webhook, ggChatId);
    }

}
