package com.vietqr.org.service;

import com.vietqr.org.dto.TransTempCountDTO;
import com.vietqr.org.entity.TransReceiveTempEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransReceiveTempService {
    TransReceiveTempEntity getLastTimeByBankId(String bankId);

    String getTransIdsByBankId(String bankId);

    void insert(TransReceiveTempEntity entity);

    int updateTransReceiveTemp(String transIds, int aftNum, long currentStartDate,
                               long lastTime, int preNum, String transId, String id);

    List<TransTempCountDTO> getTransTempCounts(List<String> bankIds);

    TransTempCountDTO getTransTempCount(String bankId);
}
