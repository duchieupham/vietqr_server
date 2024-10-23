package com.vietqr.org.service;

import com.vietqr.org.dto.QrBoxDynamicDTO;
import com.vietqr.org.dto.QrBoxListDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TerminalBankEntity;

import java.util.List;

@Service
public interface TerminalBankService {

    public int insertTerminalBank(TerminalBankEntity entity);

    public TerminalBankEntity getTerminalBankByTerminalId(String terminalId);

    public TerminalBankEntity getTerminalBankByBankAccount(String bankAccount);

    public String checkExistedTerminalAddress(String address);

    public String getTerminalAddress(String terminalName);

    public List<String> getListTerminalNames();
    public List<String> getTerminalAddresses();

    public Integer getTerminalCounting();

    public String getBankAccountByTerminalLabel(String terminalLabel);

    List<QrBoxListDTO> getQrBoxListByBankId(String bankId);

    List<QrBoxDynamicDTO> getQrBoxDynamicQrByBankId(String bankId);

    String getTerminalBankQRByBankAccount(String bankAccount);

    String getIdByBankAccount(String bankAccount);
}