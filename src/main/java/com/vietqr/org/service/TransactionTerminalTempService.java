package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionTerminalTempService {
    List<TransactionTerminalTempEntity> getAllTransactionTerminalByDateAndTerminalCode(String terminalCode,
                                                                                       String fromDate, String toDate);

    int insertTransactionTerminal(TransactionTerminalTempEntity transactionTerminalTempEntity);

    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromTime, String toTime);

    IStatisticMerchantDTO getStatisticMerchantByDate(String userId, String fromDate, String toDate);

    List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, String fromDate, String toDate);

    List<ITopTerminalDTO> getTop5TerminalByDate(String userId, String fromDate, String toDate);
}
