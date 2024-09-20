package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.repository.AccountBankReceiveRepository;
import com.vietqr.org.repository.BankTypeRepository;
import com.vietqr.org.repository.TerminalBankReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VietQRServiceImpl implements VietQRService {
    @Autowired
    private BankTypeRepository bankTypeRepository;

    @Autowired
    private AccountBankReceiveRepository accountBankReceiveRepository;

    @Autowired
    private TerminalBankReceiveRepository terminalBankReceiveRepository;

    @Override
    public ICaiBankTypeQR getCaiBankTypeById(String id) {
        return bankTypeRepository.getCaiBankTypeById(id);
    }

    @Override
    public IAccountBankQR getAccountBankQR(String bankAccount, String bankTypeId) {
        return accountBankReceiveRepository.getAccountBankQR(bankAccount, bankTypeId);
    }

    @Override
    public IAccountBankInfoQR getAccountBankQRByAccountAndId(String bankAccount, String bankTypeId) {
        return accountBankReceiveRepository.getAccountBankQRByAccountAndId(bankAccount, bankTypeId);
    }

    @Override
    public ITerminalBankReceiveQR getTerminalBankReceiveQR(String subRawCode) {
        return terminalBankReceiveRepository.getTerminalBankReceiveQR(subRawCode);
    }

    @Override
    public IAccountBankUserQR getAccountBankUserQRById(String bankId) {
        return accountBankReceiveRepository.getAccountBankUserQRById(bankId);
    }
}
