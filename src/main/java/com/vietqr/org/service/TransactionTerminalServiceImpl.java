package com.vietqr.org.service;

import com.vietqr.org.dto.RevenueTerminalDTO;
import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.entity.TransactionTerminalEntity;
import com.vietqr.org.repository.TransactionTerminalRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionTerminalServiceImpl implements TransactionTerminalService {

    @Autowired
    private TransactionTerminalRepository repo;

    @Override
    public List<TransactionTerminalEntity> getAllTransactionTerminalByDateAndTerminalCode(String terminalCode,
                                                                                          String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.findAllByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int insertTransactionTerminal(TransactionTerminalEntity transactionTerminalEntity) {
        return repo.save(transactionTerminalEntity) != null ? 0 : 1;
    }

    @Override
    public RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, String fromDate, String toDate) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndTime(fromDate, toDate);
        return repo.getTotalTranByTerminalCodeAndTimeBetween(terminalCode,
                startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }
}
