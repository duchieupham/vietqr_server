package com.vietqr.org.service;

import com.vietqr.org.entity.TerminalStatisticEntity;
import org.springframework.stereotype.Service;

@Service
public interface TerminalStatisticService {
    boolean saveTerminalStatistic(TerminalStatisticEntity entity);

    boolean updateTerminalStatistic(TerminalStatisticEntity entity);

    TerminalStatisticEntity findByTerminalIdAndTime(String terminalId, String date);

    long getTotalAmountPrevious(String terminalId, long time);
}
