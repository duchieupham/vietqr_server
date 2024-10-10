package com.vietqr.org.service;

import com.vietqr.org.entity.TerminalItemEntity;
import com.vietqr.org.repository.TerminalItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminalItemServiceImpl implements TerminalItemService {

    @Autowired
    private TerminalItemRepository repo;

    @Override
    public TerminalItemEntity getTerminalItemByTraceTransferAndAmount(String traceTransfer, String amount, String serviceCode) {
        return repo.getTerminalItemByTraceTransferAndAmount(traceTransfer, amount, serviceCode);
    }

    @Override
    public void insert(TerminalItemEntity entity) {
        repo.save(entity);
    }

    @Override
    public TerminalItemEntity getItemByBankAndServiceCode(String bankId, String serviceCode, String terminalCode) {
        return repo.getItemByBankAndServiceCode(bankId, serviceCode, terminalCode);
    }

    @Override
    public void removeById(String id) {
        repo.removeById(id);
    }

    @Override
    public String existsByIdServiceCodeTerminalCode(String id, String serviceCode, String terminalCode) {
        return repo.existsByIdServiceCodeTerminalCode(id, serviceCode, terminalCode);
    }

    @Override
    public TerminalItemEntity getTerminalItemByServiceCode(String serviceCode) {
        return repo.getTerminalItemByServiceCode(serviceCode);
    }
}
