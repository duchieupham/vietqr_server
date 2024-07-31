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
        return repo.getTerminalsByUserId(userId, offset);
//        return repo.getTerminalsByUserId(userId);
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
    public List<ITerminalShareDTO> getTerminalSharesByBankIds2(List<String> bankIds, String userId) {
        return repo.getTerminalSharesByBankIds2(bankIds, userId);
    }

    @Override
    public List<TerminalResponseInterfaceDTO> getTerminalSharesByUserId(String userId, int offset) {
        return repo.getTerminalsShareByUserId(userId, offset);
//        return repo.getTerminalsShareByUserId(userId);
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
    public TerminalEntity getTerminalByTerminalCode(String terminalCode) {
        return repo.getTerminalByTerminalCode(terminalCode);
    }

    @Override
    public String getTerminalByTraceTransfer(String traceTransfer) {
        return repo.getTerminalByTraceTransfer(traceTransfer);
    }

    @Override
    public List<ITerminalDetailWebDTO> getTerminalByUserId(String userId, int offset, String value) {
        return repo.getTerminalWebByUserId(userId, offset, value);
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
    public void insertAllTerminal(List<TerminalEntity> terminalEntities) {
        repo.saveAll(terminalEntities);
    }

    @Override
    public String checkExistedRawTerminalCode(String terminalCode) {
        return repo.checkExistedRawTerminalCode(terminalCode);
    }

    @Override
    public List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndUserId(String userId, String bankId) {
        return repo.getListTerminalResponseByBankIdAndUserId(userId, bankId);
    }

    @Override
    public String getTerminalCodeByTerminalCode(String value) {
        return repo.getTerminalCodeByTerminalCode(value);
    }

    @Override
    public TerminalEntity getTerminalByTerminalBankReceiveCode(String terminalCode) {
        return repo.getTerminalByTerminalBankReceiveCode(terminalCode);
    }

    @Override
    public int countNumberOfTerminalByUserIdOwner(String userId) {
        return repo.countNumberOfTerminalByUserIdOwner(userId);
    }

    @Override
    public List<TerminalMapperDTO> getTerminalsByUserIdAndMerchantId(String userId, String merchantId) {
        return repo.getTerminalsByUserIdAndMerchantId(userId, merchantId);
    }

    @Override
    public List<TerminalMapperDTO> getTerminalsByUserIdAndMerchantIdOwner(String userId, String merchantId) {
        return repo.getTerminalsByUserIdAndMerchantIdOwner(userId, merchantId);
    }

    @Override
    public List<String> getAllCodeByMerchantId(String merchantId, String userId) {
        return repo.getAllCodeByMerchantId(merchantId, userId);
    }

    @Override
    public List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantId(String merchantId, String userId, int offset) {
        return repo.getListTerminalByMerchantId(merchantId, userId, offset);
    }

    @Override
    public List<String> getAllCodeByMerchantIdOwner(String merchantId, String userId) {
        return repo.getAllCodeByMerchantIdOwner(merchantId, userId);
    }

    @Override
    public List<String> getAllCodeByMerchantIdIn(String merchantId, String userId) {
        return repo.getAllCodeByMerchantIdIn(merchantId, userId);
    }

    @Override
    public List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankIdOwner(String userId, String bankId) {
        return repo.getTerminalsByUserIdAndBankIdOwner(userId, bankId);
    }

    @Override
    public List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantIdOwner(String merchantId, String userId, int offset) {
        return repo.getListTerminalByMerchantIdOwner(merchantId, userId, offset);
    }

    @Override
    public List<ITerminalExportDTO> getTerminalExportByUserId(String userId) {
        return repo.getTerminalExportByUserId(userId);
    }

    @Override
    public List<ITerminalExportDTO> getTerminalExportByCode(String terminalCode) {
        return repo.getTerminalExportByCode(terminalCode);
    }

    @Override
    public List<ITerminalExportDTO> getTerminalByUserIdHaveRole(String userId) {
        return repo.getTerminalByUserIdHaveRole(userId);
    }

    @Override
    public String getUserIdByTerminalId(String terminalId) {
        return repo.getUserIdByTerminalId(terminalId);
    }

    @Override
    public String checkExistedTerminalRawCode(String code) {
        return repo.checkExistedRawTerminalCode(code);
    }

    @Override
    public List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndMerchantId(String merchantId, String bankId) {
        return repo.getListTerminalResponseByBankIdAndMerchantId(merchantId, bankId);
    }

    @Override
    public List<ITerminalSyncDTO> getTerminalByMidSync(String mid, int offset, int size) {
        return repo.getTerminalByMidSync(mid, offset, size);
    }

    @Override
    public int countTerminalByMidSync(String midForSearch) {
        return repo.countTerminalByMidSync(midForSearch);
    }

    @Override
    public List<String> checkExistedTerminalRawCodes(List<String> rawCodes) {
        return repo.checkExistedTerminalRawCodes(rawCodes);
    }

    @Override
    public String checkExistedPublishId(String publishId) {
        return repo.checkExistedPublishId(publishId);
    }

    @Override
    public List<String> getAllCodeByNameAndBankId(String name, String bankId) {
        return repo.getAllCodeByNameAndBankId(name, bankId);
    }

    @Override
    public TerminalEntity getTerminalByTerminalId(String terminalId) {
        return repo.getTerminalByTerminalId(terminalId);
    }
}
