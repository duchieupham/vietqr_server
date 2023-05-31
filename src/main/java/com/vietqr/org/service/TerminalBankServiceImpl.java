package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.repository.TerminalBankRepository;

@Service
public class TerminalBankServiceImpl implements TerminalBankService {

    @Autowired
    TerminalBankRepository repository;

    @Override
    public int insertTerminalBank(TerminalBankEntity entity) {
        return repository.save(entity) == null ? 0 : 1;
    }

    @Override
    public TerminalBankEntity getTerminalBankByTerminalId(String terminalId) {
        return repository.getTerminalBankByTerminalId(terminalId);
    }

}
