package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalBankEntity;

@Service
public interface TerminalBankService {

    public int insertTerminalBank(TerminalBankEntity entity);

    public TerminalBankEntity getTerminalBankByTerminalId(String terminalId);
}