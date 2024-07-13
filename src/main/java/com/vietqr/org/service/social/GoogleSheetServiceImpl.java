package com.vietqr.org.service.social;

import com.vietqr.org.dto.GoogleSheetInfoDetailDTO;
import com.vietqr.org.entity.GoogleSheetEntity;
import com.vietqr.org.repository.GoogleSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleSheetServiceImpl implements GoogleSheetService {

    @Autowired
    GoogleSheetRepository repo;



    @Override
    public void updateGoogleSheet(String webhook, String googleSheetId) {
        repo.updateWebhook(googleSheetId, webhook);
    }

    @Override
    public int countGoogleSheetsByUserId(String userId) {
        return repo.countGoogleSheetsByUserId(userId);
    }

    @Override
    public List<GoogleSheetInfoDetailDTO> getGoogleSheetsByUserIdWithPagination(String userId, int offset, int size) {
        return repo.getGoogleSheetsByUserIdWithPagination(userId, offset, size);
    }

    @Override
    public GoogleSheetEntity getGoogleSheetByUserId(String userId) {
        return repo.getGoogleSheetByUserId(userId);
    }

    @Override
    public GoogleSheetEntity getGoogleSheetById(String id) {
        return repo.getGoogleSheetById(id);
    }

    @Override
    public void insert(GoogleSheetEntity entity) {
        repo.save(entity);
    }

    @Override
    public void updateGoogleSheet(GoogleSheetEntity entity) {
        repo.save(entity);
    }

    @Override
    public void removeGoogleSheet(String id) {
        repo.deleteById(id);
    }

    @Override
    public GoogleSheetEntity getGoogleSheetByWebhook(String webhook) {
        return repo.findByWebhook(webhook);
    }
}