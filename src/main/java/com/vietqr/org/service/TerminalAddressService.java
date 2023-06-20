package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalAddressEntity;

@Service
public interface TerminalAddressService {

    public List<TerminalAddressEntity> getTerminalAddressByTerminalBankId(String terminalBankId);
}
