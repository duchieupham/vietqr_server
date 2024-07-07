package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TelBankDTO;
import com.vietqr.org.entity.TelegramAccountBankEntity;

@Service
public interface TelegramAccountBankService {

    public int insert(TelegramAccountBankEntity entity);

    public List<TelBankDTO> getTelAccBanksByTelId(String telId);

    public void removeTelAccBankByTelId(String telId);

    public void removeTelAccBankByTelIdAndBankId(String telId, String bankId);

    // for case send msg
    public List<String> getChatIdsByBankId(String bankId);

    void updateWebHookTelegram(String chatId, String teleId);
}
