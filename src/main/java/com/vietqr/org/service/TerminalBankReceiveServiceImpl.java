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
    public void insert(TerminalBankReceiveEntity terminalBankReceiveEntity) {
        repo.save(terminalBankReceiveEntity);
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

    @Override
    public String checkExistedTerminalCode(String code) {
        return repo.checkExistedTerminalCode(code);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankByTerminalId(String terminalId) {
        return repo.getTerminalBankByTerminalId(terminalId);
    }

    @Override
    public String getTerminalCodeByRawTerminalCode(String value) {
        return repo.getTerminalCodeByRawTerminalCode(value);
    }

    @Override
    public List<String> getTerminalCodeByMainTerminalCode(String terminalCodeForSearch) {
        return repo.getTerminalCodeByMainTerminalCode(terminalCodeForSearch);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByTraceTransfer(String traceTransfer) {
        return repo.getTerminalBankReceiveByTraceTransfer(traceTransfer);
    }

    @Override
    public List<String> getTerminalCodeByMainTerminalCodeList(List<String> terminalCodeAccess) {
        return repo.getTerminalCodeByMainTerminalCodeList(terminalCodeAccess);
    }

    @Override
    public String getTerminalBankReceiveByTerminalCode(String terminalCode) {
        return repo.getTerminalBankReceiveByTerminalCode(terminalCode);
    }
}