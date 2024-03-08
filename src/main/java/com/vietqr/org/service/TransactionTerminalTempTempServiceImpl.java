package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import com.vietqr.org.repository.TransactionTerminalTempRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionTerminalTempTempServiceImpl implements TransactionTerminalTempService {

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
    public RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.getTotalTranByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public IStatisticMerchantDTO getStatisticMerchantByDate(String userId, String fromDate, String toDate) {
        return repo.getStatisticMerchantByDate(userId, fromDate, toDate);
    }

    @Override
    public List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, String fromDate, String toDate) {
        return repo.getStatisticMerchantByDateEveryHour(userId, fromDate, toDate);
    }

    @Override
    public List<ITopTerminalDTO> getTop5TerminalByDate(String userId, String fromDate, String toDate) {
        return repo.getTop5TerminalByDate(userId, fromDate, toDate);
    }
}
