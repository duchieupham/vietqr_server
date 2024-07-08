package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.GoogleChatInfoDetailDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.GoogleChatEntity;

@Service
public interface GoogleChatService {
    public int insert(GoogleChatEntity entity);

    public GoogleChatEntity getGoogleChatById(String id);
    GoogleChatEntity getGoogleChatByWebhook(String webhook);

    public void removeGoogleChat(String id);

    GoogleChatEntity getGoogleChatsByUserId(String userId);

    void updateGoogleChat(String webhook, String id);

    void updateGoogleChat(GoogleChatEntity googleChatEntity);

    int countGoogleChatsByUserId(String userId);

    List<GoogleChatInfoDetailDTO> getGoogleChatsByUserIdWithPagination(String userId, int offset, int size);
}
