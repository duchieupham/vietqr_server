package com.vietqr.org.service;

import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TerminalBankReceiveService {
    void insertAllTerminalBankReceive(List<TerminalBankReceiveEntity> terminalBankReceiveEntities);

    void insert(TerminalBankReceiveEntity terminalBankReceiveEntity);

    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalIdAndBankId(String terminalId, String bankId);

    String getTerminalByTraceTransfer(String traceTransfer);

    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalId(String terminalId);

    String checkExistedTerminalCode(String code);

    TerminalBankReceiveEntity getTerminalBankByTerminalId(String terminalId);
}
