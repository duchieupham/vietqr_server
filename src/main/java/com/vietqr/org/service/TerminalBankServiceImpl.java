package com.vietqr.org.service;

import com.vietqr.org.dto.QrBoxDynamicDTO;
import com.vietqr.org.dto.QrBoxListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.repository.TerminalBankRepository;

import java.util.List;

@Service
public class TerminalBankServiceImpl implements TerminalBankService {

    @Autowired
    TerminalBankRepository repo;

    @Override
    public int insertTerminalBank(TerminalBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public TerminalBankEntity getTerminalBankByTerminalId(String terminalId) {
        return repo.getTerminalBankByTerminalId(terminalId);
    }

    @Override
    public TerminalBankEntity getTerminalBankByBankAccount(String bankAccount) {
        return repo.getTerminalBankByBankAccount(bankAccount);
    }

    @Override
    public String checkExistedTerminalAddress(String address) {
        return repo.checkExistedTerminalAddress(address);
    }

    @Override
    public String getTerminalAddress(String terminalName) {
        return repo.getTerminalAddress(terminalName);
    }

    @Override
    public List<String> getListTerminalNames() {
        return repo.getListTerminalNames();
    }

    @Override
    public List<String> getTerminalAddresses() {
        return repo.getTerminalAddresses();
    }

    @Override
    public Integer getTerminalCounting() {
        return repo.getTerminalCounting();
    }

    @Override
    public String getBankAccountByTerminalLabel(String terminalLabel) {
        return repo.getBankAccountByTerminalLabel(terminalLabel);
    }

    @Override
    public List<QrBoxListDTO> getQrBoxListByBankId(String bankId) {
        return repo.getQrBoxListByBankId(bankId);
    }

    @Override
    public List<QrBoxDynamicDTO> getQrBoxDynamicQrByBankId(String bankId) {
        return repo.getQrBoxDynamicQrByBankId(bankId);
    }

    @Override
    public String getTerminalBankQRByBankAccount(String bankAccount) {
        return repo.getTerminalBankQRByBankAccount(bankAccount);
    }

}
