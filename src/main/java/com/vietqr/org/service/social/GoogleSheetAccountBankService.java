package com.vietqr.org.service.social;

import com.vietqr.org.dto.GoogleSheetBankDTO;
import com.vietqr.org.entity.GoogleSheetAccountBankEntity;

import java.util.List;

public interface GoogleSheetAccountBankService {
    int insert(GoogleSheetAccountBankEntity entity);
    String checkExistedBankId(String bankId, String googleSheetId);
    void deleteByBankIdAndGoogleSheetId(String bankId, String googleSheetId);
    void deleteByGoogleSheetId(String googleSheetId);
    List<GoogleSheetBankDTO> getGoogleSheetAccountBanks(String googleSheetId);
    List<String> getWebhooksByBankId(String bankId);
    void updateWebHookGoogleSheet(String webhook, String googleSheetId);
}
