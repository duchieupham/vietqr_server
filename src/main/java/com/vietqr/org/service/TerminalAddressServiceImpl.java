package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalAddressEntity;
import com.vietqr.org.repository.TerminalAddressRepository;

@Service
public class TerminalAddressServiceImpl implements TerminalAddressService {

    @Autowired
    TerminalAddressRepository repository;

    @Override
    public List<TerminalAddressEntity> getTerminalAddressByTerminalBankId(String terminalBankId) {
        return repository.getTerminalAddressByTerminalBankId(terminalBankId);
    }

}
