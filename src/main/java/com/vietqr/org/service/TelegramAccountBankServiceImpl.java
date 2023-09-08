package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TelBankDTO;
import com.vietqr.org.entity.TelegramAccountBankEntity;
import com.vietqr.org.repository.TelegramAccountBankRepository;

@Service
public class TelegramAccountBankServiceImpl implements TelegramAccountBankService {

    @Autowired
    TelegramAccountBankRepository repo;

    @Override
    public int insert(TelegramAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<TelBankDTO> getTelAccBanksByTelId(String telId) {
        return repo.getTelAccBanksByTelId(telId);
    }

    @Override
    public void removeTelAccBankByTelId(String telId) {
        repo.removeTelAccBankByTelId(telId);
    }

    @Override
    public List<String> getChatIdsByBankId(String bankId) {
        return repo.getChatIdsByBankId(bankId);
    }

    @Override
    public void removeTelAccBankByTelIdAndBankId(String telId, String bankId) {
        repo.removeTelAccBankByTelIdAndBankId(telId, bankId);
    }

}
