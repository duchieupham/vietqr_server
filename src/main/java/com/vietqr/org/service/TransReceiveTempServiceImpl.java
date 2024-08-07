package com.vietqr.org.service;

import com.vietqr.org.entity.TransReceiveTempEntity;
import com.vietqr.org.repository.TransReceiveTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransReceiveTempServiceImpl implements TransReceiveTempService {
    @Autowired
    private TransReceiveTempRepository repo;
    @Override
    public TransReceiveTempEntity getLastTimeByBankId(String bankId) {
        return repo.getLastTimeByBankId(bankId);
    }

    @Override
    public String getTransIdsByBankId(String bankId) {
        return repo.getTransIdsByBankId(bankId);
    }

    @Override
    public void insert(TransReceiveTempEntity entity) {
        repo.save(entity);
    }

    @Override
    public int updateTransReceiveTemp(String transIds, int aftNum, long currentStartDate,
                                      long lastTime, int preNum, String transId, String id) {
        return repo.updateTransReceiveTemp(transIds, aftNum, currentStartDate, lastTime, preNum, transId, id);
    }
}
