package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.ISocialMediaDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TelegramEntity;

@Service
public interface TelegramService {

    public List<ISocialMediaDTO> getSocialInfoByUserId(String userId);

    public int insertTelegram(TelegramEntity entity);

    public List<TelegramEntity> getTelegramsByUserId(String userId);

    public TelegramEntity getTelegramById(String id);

    public void removeTelegramById(String id);

    void updateTelegram(TelegramEntity telegramEntity);

    TelegramEntity getTelegramByChatId(String chatId);

}
