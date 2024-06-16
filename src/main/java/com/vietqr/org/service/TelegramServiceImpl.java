package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.ISocialMediaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TelegramEntity;
import com.vietqr.org.repository.TelegramRepository;

@Service
public class TelegramServiceImpl implements TelegramService {

    @Autowired
    TelegramRepository repo;

    @Override
    public List<ISocialMediaDTO> getSocialInfoByUserId(String userId) {
        return repo.getSocialInfoByUserId(userId);
    }

    @Override
    public int insertTelegram(TelegramEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<TelegramEntity> getTelegramsByUserId(String userId) {
        return repo.getTelegramsByUserId(userId);
    }

    @Override
    public void removeTelegramById(String id) {
        repo.removeTelegramById(id);
    }

    @Override
    public TelegramEntity getTelegramById(String id) {
        return repo.getTelegramById(id);
    }

}
