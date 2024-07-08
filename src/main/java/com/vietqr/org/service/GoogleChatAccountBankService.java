package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.GoogleChatBankDTO;
import com.vietqr.org.entity.GoogleChatAccountBankEntity;

@Service
public interface GoogleChatAccountBankService {

    public int insert(GoogleChatAccountBankEntity entity);

    public String checkExistedBankId(String bankId, String googleChatId);

    public void deleteByBankIdAndGoogleChatId(String bankId, String googleChatId);

    public void deleteByGoogleChatId(String googleChatId);

    public List<GoogleChatBankDTO> getGoogleAccountBanks(String googleChatId);

    List<String> getWebhooksByBankId(String bankId);

    void updateWebHookGoogleChat(String webhook, String ggChatId);
}
