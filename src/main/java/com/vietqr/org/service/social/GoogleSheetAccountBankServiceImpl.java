package com.vietqr.org.service.social;

import com.vietqr.org.dto.GoogleSheetBankDTO;
import com.vietqr.org.entity.GoogleSheetAccountBankEntity;
import com.vietqr.org.repository.GoogleSheetAccountBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleSheetAccountBankServiceImpl implements GoogleSheetAccountBankService {

    @Autowired
    GoogleSheetAccountBankRepository repo;

    @Override
    public int insert(GoogleSheetAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedBankId(String bankId, String googleSheetId) {
        return repo.checkExistedBankId(bankId, googleSheetId);
    }

    @Override
    public void deleteByBankIdAndGoogleSheetId(String bankId, String googleSheetId) {
        repo.deleteByBankIdAndGoogleSheetId(bankId, googleSheetId);
    }

    @Override
    public void deleteByGoogleSheetId(String googleSheetId) {
        repo.deleteByGoogleSheetId(googleSheetId);
    }

    @Override
    public List<GoogleSheetBankDTO> getGoogleSheetAccountBanks(String googleSheetId) {
        return repo.getGoogleSheetAccountBanks(googleSheetId);
    }

    @Override
    public List<String> getWebhooksByBankId(String bankId) {
        return repo.getWebhooksByBankId(bankId);
    }

    @Override
    public void updateWebHookGoogleSheet(String webhook, String googleSheetId) {
        repo.updateWebHookGoogleSheet(webhook, googleSheetId);
    }
}