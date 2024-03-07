package com.vietqr.org.service;

import com.vietqr.org.dto.RevenueTerminalDTO;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionTerminalTempService {
    List<TransactionTerminalTempEntity> getAllTransactionTerminalByDateAndTerminalCode(String terminalCode,
                                                                                       String fromDate, String toDate);

    int insertTransactionTerminal(TransactionTerminalTempEntity transactionTerminalTempEntity);

    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromTime, String toTime);
}
