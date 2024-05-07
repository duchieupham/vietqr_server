package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.GoogleChatEntity;

@Service
public interface GoogleChatService {
    public int insert(GoogleChatEntity entity);

    public GoogleChatEntity getGoogleChatById(String id);

    public void removeGoogleChat(String id);

    GoogleChatEntity getGoogleChatsByUserId(String userId);
}
