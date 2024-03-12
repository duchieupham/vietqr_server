package com.vietqr.org.service;

import com.vietqr.org.entity.TerminalBankReceiveEntity;
import com.vietqr.org.repository.TerminalBankReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TerminalBankReceiveServiceImpl implements TerminalBankReceiveService {

    @Autowired
    private TerminalBankReceiveRepository repo;
    @Override
    public void insertAllTerminalBankReceive(List<TerminalBankReceiveEntity> terminalBankReceiveEntities) {
        repo.saveAll(terminalBankReceiveEntities);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByTerminalIdAndBankId(String terminalId, String bankId) {
        return repo.getTerminalBankReceiveByTerminalIdAndBankId(terminalId, bankId);
    }

    @Override
    public String getTerminalByTraceTransfer(String traceTransfer) {
        return repo.getTerminalByTraceTransfer(traceTransfer);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByTerminalId(String terminalId) {
        return repo.getTerminalBankReceiveByTerminalId(terminalId);
    }
}