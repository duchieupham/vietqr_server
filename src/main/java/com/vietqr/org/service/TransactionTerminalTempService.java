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

    RevenueTerminalDTO getTotalTranByUserIdAndTimeBetween(List<String> terminalCode, String fromDate, String toDate);

    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(List<String> terminalCode, String fromTime, String toTime);

    IStatisticMerchantDTO getStatisticMerchantByDate(List<String> listTerminalCode, String fromDate, String toDate);

    List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, String fromDate, String toDate);

    List<ITopTerminalDTO> getTopTerminalByDate(String userId, String fromDate, String toDate, int pazeSize);

    List<IStatisticTerminalOverViewDTO> getStatisticMerchantByDateEveryTerminal(String userId,
                                                                                String fromDate, String toDate, int offset);

    RevenueTerminalDTO getTotalTranByUserIdAndTimeBetweenWithCurrentTime(List<String> terminalCode, long startTime, long currentTime);

    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetweenWithCurrentTime(List<String> terminalCode, long fromDate, long currentDateTimeAsNumber);

    TransactionTerminalTempEntity getTempByTransactionId(String transactionId);

    IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId, String fromDate, String toDate);

    long getTotalAmountByTerminalCodeAndTime(String terminalCode, String fromDate, String toDate);

    long getTotalTranByTerminalCodeWithCurrentTime(String terminalCode, long fromDate, long currentDateTimeAsNumber);

}
