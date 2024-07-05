package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.GoogleChatEntity;
import com.vietqr.org.repository.GoogleChatRepository;

@Service
public class GoogleChatServiveImpl implements GoogleChatService {

    @Autowired
    GoogleChatRepository repo;

    @Override
    public int insert(GoogleChatEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public GoogleChatEntity getGoogleChatById(String id) {
        return repo.getGoogleChatById(id);
    }

    @Override
    public GoogleChatEntity getGoogleChatByWebhook(String webhook) {
        return repo.getGoogleChatsByWebhook(webhook);
    }

    @Override
    public void removeGoogleChat(String id) {
        repo.removeGoogleChat(id);
    }

    @Override
    public GoogleChatEntity getGoogleChatsByUserId(String userId) {
        return repo.getGoogleChatsByUserId(userId);
    }

    @Override
    public void updateGoogleChat(GoogleChatEntity googleChatEntity) {
        repo.save(googleChatEntity);
    }

}
