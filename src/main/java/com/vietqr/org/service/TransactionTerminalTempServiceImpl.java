package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import com.vietqr.org.repository.TransactionTerminalTempRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionTerminalTempServiceImpl implements TransactionTerminalTempService {

    @Autowired
    private TransactionTerminalTempRepository repo;

    @Override
    public List<TransactionTerminalTempEntity> getAllTransactionTerminalByDateAndTerminalCode(String terminalCode,
                                                                                              String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.findAllByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int insertTransactionTerminal(TransactionTerminalTempEntity transactionTerminalTempEntity) {
        return repo.save(transactionTerminalTempEntity) != null ? 0 : 1;
    }

    @Override
    public RevenueTerminalDTO getTotalTranByUserIdAndTimeBetween(String userId, String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.getTotalTranByUserAndTimeBetween(userId,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.getTotalTranByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public IStatisticMerchantDTO getStatisticMerchantByDate(String userId, String fromDate, String toDate) {
        long from = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long to = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getStatisticMerchantByDate(userId,
                from - DateTimeUtil.GMT_PLUS_7_OFFSET,
                to - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, String fromDate, String toDate) {
        long from = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long to = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getStatisticMerchantByDateEveryHour(userId,
                from - DateTimeUtil.GMT_PLUS_7_OFFSET,
                to - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<ITopTerminalDTO> getTopTerminalByDate(String userId, String fromDate, String toDate, int pageSize) {
        long from = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long to = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTopTerminalByDate(userId,
                from - DateTimeUtil.GMT_PLUS_7_OFFSET,
                to - DateTimeUtil.GMT_PLUS_7_OFFSET,
                pageSize);
    }

    @Override
    public List<IStatisticTerminalOverViewDTO> getStatisticMerchantByDateEveryTerminal(String userId,
                                                                                       String fromDate, String toDate, int offset) {

        return repo.getStatisticMerchantByDateEveryTerminal(userId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public RevenueTerminalDTO getTotalTranByUserIdAndTimeBetweenWithCurrentTime(String userId, String fromDate, long currentTime) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, fromDate);
        return repo.getTotalTranByUserAndTimeBetween(userId,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                currentTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetweenWithCurrentTime(String terminalCode, String fromDate, long currentDateTimeAsNumber) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, fromDate);
        return repo.getTotalTranByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                currentDateTimeAsNumber - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransactionTerminalTempEntity getTempByTransactionId(String transactionId) {
        return repo.findByTransactionId(transactionId);
    }
}
