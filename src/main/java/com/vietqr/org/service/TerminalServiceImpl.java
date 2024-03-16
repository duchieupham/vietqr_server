package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalEntity;
import com.vietqr.org.repository.TerminalRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminalServiceImpl implements TerminalService {
    @Autowired
    private TerminalRepository repo;

    @Override
    public int insertTerminal(TerminalEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public ITerminalDetailResponseDTO getTerminalById(String id) {
        return repo.getTerminalById(id);
    }

    @Override
    public void removeTerminalById(String id) {
        repo.removeTerminalById(id);
    }

    @Override
    public String checkExistedTerminal(String code) {
        return repo.checkTerminalExisted(code);
    }

    @Override
    public List<TerminalResponseInterfaceDTO> getTerminalsByUserId(String userId, int offset) {
//        return repo.getTerminalsByUserId(userId, offset);
        return repo.getTerminalsByUserId(userId);
    }

    @Override
    public int countNumberOfTerminalByUserId(String userId) {
        return repo.countNumberOfTerminalByUserId(userId);
    }

    @Override
    public List<ITerminalShareDTO> getTerminalSharesByBankIds(List<String> bankIds, String userId) {
        return repo.getTerminalSharesByBankIds(bankIds, userId);
    }

    @Override
    public List<TerminalResponseInterfaceDTO> getTerminalSharesByUserId(String userId, int offset) {
//        return repo.getTerminalsShareByUserId(userId, offset);
        return repo.getTerminalsShareByUserId(userId);
    }

    @Override
    public int countNumberOfTerminalShareByUserId(String userId) {
        return repo.countNumberOfTerminalShareByUserId(userId);
    }

    @Override
    public TerminalEntity findTerminalById(String id) {
        return repo.findTerminalById(id);
    }

    @Override
    public List<TerminalResponseInterfaceDTO> getTerminalsByUserIdAndBankId(String userId, String bankId, int offset) {
//        return repo.getTerminalsByUserIdAndBankId(userId, bankId, offset);
        return repo.getTerminalsByUserIdAndBankIdOffset(userId, bankId);
    }

    @Override
    public List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankId(String userId, String bankId) {
        return repo.getTerminalsByUserIdAndBankId(userId, bankId);
    }

    @Override
    public int countNumberOfTerminalByUserIdAndBankId(String userId, String bankId) {
        return repo.countNumberOfTerminalByUserIdAndBankId(userId, bankId);
    }

    @Override
    public List<String> getUserIdsByTerminalCode(String terminalCode) {
        return repo.getUserIdsByTerminalCode(terminalCode);
    }

    @Override
    public TerminalEntity getTerminalByTerminalCode(String terminalCode, String bankAccount) {
        return repo.getTerminalByTerminalCodeAndBankAccount(terminalCode, bankAccount);
    }

    @Override
    public String getTerminalByTraceTransfer(String traceTransfer) {
        return repo.getTerminalByTraceTransfer(traceTransfer);
    }

    @Override
    public List<TerminalEntity> getAllTerminalNoQRCode() {
        return repo.getAllTerminalNoQRCode();
    }

    @Override
    public List<ITerminalDetailWebDTO> getTerminalByUserId(String userId, int offset, String value) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndCurrentDate();
        return repo.getTerminalWebByUserId(userId, offset, value);
    }

    @Override
    public List<ITerminalDetailWebDTO> getTerminalByUserIdAndMerchantId(String merchantId, String userId, int offset, String value) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndCurrentDate();
        return repo.getTerminalWebByUserIdAndMerchantId(merchantId, userId, offset, value,
                dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public ITerminalBankResponseDTO getTerminalResponseById(String terminalId, String userId) {
        return repo.getTerminalResponseById(terminalId, userId);
    }

    @Override
    public ITerminalWebResponseDTO getTerminalWebById(String terminalId) {
        return repo.getTerminalWebById(terminalId);
    }

    @Override
    public String checkExistedTerminalIntoMerchant(String terminalId, String merchantId) {
        return repo.checkExistedTerminalIntoMerchant(terminalId, merchantId);
    }

    @Override
    public TerminalEntity findTerminalByPublicId(String terminalId) {
        return repo.findTerminalByPublicId(terminalId);
    }

    @Override
    public void insertAllTerminal(List<TerminalEntity> terminalEntities) {
        repo.saveAll(terminalEntities);
    }

    @Override
    public List<ITerminalTidResponseDTO> getTerminalByMerchantId(String merchantId, int offset, int size) {
        return repo.getTerminalByMerchantId(merchantId, offset, size);
    }

    @Override
    public String checkExistedRawTerminalCode(String terminalCode) {
        return repo.checkExistedRawTerminalCode(terminalCode);
    }

    @Override
    public List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndUserId(String userId, String bankId) {
        return repo.getListTerminalResponseByBankIdAndUserId(userId, bankId);
    }
}
