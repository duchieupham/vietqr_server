package com.vietqr.org.service.social;

import com.vietqr.org.dto.GoogleSheetInfoDetailDTO;
import com.vietqr.org.entity.GoogleSheetEntity;

import java.util.List;

public interface GoogleSheetService {

    void updateGoogleSheet(String webhook, String googleSheetId);
    int countGoogleSheetsByUserId(String userId);
    List<GoogleSheetInfoDetailDTO> getGoogleSheetsByUserIdWithPagination(String userId, int offset, int size);
    GoogleSheetEntity getGoogleSheetByUserId(String userId);
    GoogleSheetEntity getGoogleSheetById(String id);
    void insert(GoogleSheetEntity entity);
    void updateGoogleSheet(GoogleSheetEntity entity);
    void removeGoogleSheet(String id);
    GoogleSheetEntity getGoogleSheetByWebhook(String webhook);
}
