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

    @Override
    public TerminalAddressEntity getTerminalAddressByBankIdAndCustomerSyncId(String bankId, String customerSyncId) {
        return repository.getTerminalAddressByBankIdAndCustomerSyncId(bankId, customerSyncId);
    }

    @Override
    public TerminalAddressEntity getTerminalAddressByBankIdAndTerminalBankId(String bankId) {
        return repository.getTerminalAddressByBankIdAndTerminalBankId(bankId);
    }

    @Override
    public void removeBankAccountFromCustomerSync(String bankId, String customerSyncId) {
        repository.removeBankAccountFromCustomerSync(bankId, customerSyncId);
    }

    @Override
    public int insert(TerminalAddressEntity entity) {
        return repository.save(entity) == null ? 0 : 1;
    }

}
