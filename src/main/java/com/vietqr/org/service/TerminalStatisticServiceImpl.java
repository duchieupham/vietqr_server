package com.vietqr.org.service;

import com.vietqr.org.repository.TerminalStatisticRepository;
import com.vietqr.org.entity.TerminalStatisticEntity;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminalStatisticServiceImpl implements TerminalStatisticService {

    @Autowired
    private TerminalStatisticRepository repo;

    @Override
    public boolean saveTerminalStatistic(TerminalStatisticEntity entity) {
        return repo.save(entity) != null;
    }

    @Override
    public boolean updateTerminalStatistic(TerminalStatisticEntity entity) {
        return repo.updateByTerminalIdAndTimeAndVersion(
                entity.getTerminalId(), entity.getTime(),
                entity.getVersion(), entity.getTotalTrans(),
                entity.getTotalAmount()) > 0;
    }

    @Override
    public TerminalStatisticEntity findByTerminalIdAndTime(String terminalId, String date) {
        long time = DateTimeUtil.getDateTimeAsLongInt(date);
        return repo.findByTerminalIdAndTime(terminalId, time);
    }
}
