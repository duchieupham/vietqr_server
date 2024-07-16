package com.vietqr.org.service;

import com.vietqr.org.entity.TerminalItemEntity;
import org.springframework.stereotype.Service;

@Service
public interface TerminalItemService {
    TerminalItemEntity getTerminalItemByTraceTransferAndAmount(String traceTransfer, String amount, String serviceCode);

    void insert(TerminalItemEntity entity);
}
