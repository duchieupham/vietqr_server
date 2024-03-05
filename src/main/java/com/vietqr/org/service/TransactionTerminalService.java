package com.vietqr.org.service;

import com.vietqr.org.dto.RevenueTerminalDTO;
import com.vietqr.org.entity.TransactionTerminalEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionTerminalService {
    List<TransactionTerminalEntity> getAllTransactionTerminalByDateAndTerminalCode(String terminalCode,
                                                                                   String fromDate, String toDate);

    int insertTransactionTerminal(TransactionTerminalEntity transactionTerminalEntity);

    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromTime, String toTime);
}
