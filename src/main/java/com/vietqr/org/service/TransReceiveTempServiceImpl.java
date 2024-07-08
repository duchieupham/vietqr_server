package com.vietqr.org.service;

import com.vietqr.org.dto.TransTempCountDTO;
import com.vietqr.org.entity.TransReceiveTempEntity;
import com.vietqr.org.repository.TransReceiveTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransReceiveTempServiceImpl implements TransReceiveTempService {
    @Autowired
    private TransReceiveTempRepository repo;
    @Override
    public TransReceiveTempEntity getLastTimeByBankId(String bankId) {
        return repo.getLastTimeByBankId(bankId);
    }

    @Override
    public void insert(TransReceiveTempEntity entity) {
        repo.save(entity);
    }

    @Override
    public List<TransTempCountDTO> getTransTempCounts(List<String> bankIds) {
        return repo.getTransTempCounts(bankIds);
    }

    @Override
    public TransTempCountDTO getTransTempCount(String bankId) {
        return repo.getTransTempCount(bankId);
    }
}
