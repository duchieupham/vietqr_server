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
}
