package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalBankReceiveEntity;
import com.vietqr.org.repository.TerminalBankReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TerminalBankReceiveServiceImpl implements TerminalBankReceiveService {

    @Autowired
    private TerminalBankReceiveRepository repo;
    @Override
    public void insertAll(List<TerminalBankReceiveEntity> terminalBankReceiveEntities) {
        repo.saveAll(terminalBankReceiveEntities);
    }

    @Override
    public void insert(TerminalBankReceiveEntity terminalBankReceiveEntity) {
        repo.save(terminalBankReceiveEntity);
    }

    @Override
    public String getTerminalByTraceTransfer(String traceTransfer) {
        return repo.getTerminalByTraceTransfer(traceTransfer);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByTerminalId(String terminalId) {
        return repo.getTerminalBankReceiveByTerminalId(terminalId);
    }

    @Override
    public String checkExistedTerminalCode(String code) {
        return repo.checkExistedTerminalCode(code);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankByTerminalId(String terminalId) {
        return repo.getTerminalBankByTerminalId(terminalId);
    }

    @Override
    public String getTerminalCodeByRawTerminalCode(String value) {
        return repo.getTerminalCodeByRawTerminalCode(value);
    }

    @Override
    public List<String> getSubTerminalCodeByTerminalCode(String terminalCodeForSearch) {
        return repo.getTerminalCodeByMainTerminalCode(terminalCodeForSearch);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByTraceTransfer(String traceTransfer) {
        return repo.getTerminalBankReceiveByTraceTransfer(traceTransfer);
    }

    @Override
    public List<String> getTerminalCodeByMainTerminalCodeList(List<String> terminalCodeAccess) {
        return repo.getTerminalCodeByMainTerminalCodeList(terminalCodeAccess);
    }

    @Override
    public String getTerminalBankReceiveByTerminalCode(String terminalCode) {
        return repo.getTerminalBankReceiveByTerminalCode(terminalCode);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveByRawTerminalCode(String machineCode) {
        return repo.getTerminalBankReceiveByRawTerminalCode(machineCode);
    }

    @Override
    public List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId,
                                                                int offset, int size, String value) {
        return repo.getListSubTerminalByTerminalId(terminalId, offset, size, value);
    }

    @Override
    public ISubTerminalDTO getSubTerminalDetailBySubTerminalId(String subTerminalId) {
        return repo.getSubTerminalDetailBySubTerminalId(subTerminalId);
    }

    @Override
    public int countSubTerminalByTerminalId(String terminalId, String value) {
        return repo.countSubTerminalByTerminalId(terminalId, value);
    }

    @Override
    public List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId) {
        return repo.getListSubTerminalByTerminalId(terminalId);
    }

    @Override
    public List<ISubTerminalResponseDTO> getListSubTerminalByTerId(String terminalId) {
        return repo.getListSubTerminalByTerId(terminalId);
    }

    @Override
    public List<TerminalBankReceiveDTO> getTerminalBankReceiveResponseByTerminalId(String terminalId) {
        return repo.getTerminalBankReceiveResponseByTerminalId(terminalId);
    }

    @Override
    public ITerminalBankResponseDTO getTerminalBanksByTerminalId(String terminalId) {
        return repo.getTerminalBanksByTerminalId(terminalId);
    }

    @Override
    public List<ITerminalBankResponseDTO> getTerminalBanksByTerminalIds(List<String> terminalIds) {
        return repo.getTerminalBanksByTerminalIds(terminalIds);
    }

    @Override
    public List<IBankShareResponseDTO> getTerminalBankByUserId(String userId, int offset) {
        return repo.getTerminalBankByUserId(userId, offset);
    }

    @Override
    public int countNumberOfBankShareByUserId(String userId) {
        return repo.countNumberOfBankShareByUserId(userId);
    }

    @Override
    public List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset) {
        return repo.getTerminalBankShareByUserId(userId, offset);
    }

    @Override
    public void removeTerminalBankReceiveByTerminalId(String terminalId) {
        repo.removeTerminalBankReceiveByTerminalId(terminalId);
    }

    @Override
    public List<String> getTerminalCodeByUserIdAndBankId(String userId, String bankId) {
        return repo.getTerminalCodeByUserIdAndBankId(userId, bankId);
    }

    @Override
    public List<String> getTerminalCodeByUserIdAndBankIdNoTerminal(String userId, String bankId) {
        return repo.getTerminalCodeByUserIdAndBankIdNoTerminal(userId, bankId);
    }

    @Override
    public String getBankIdByTerminalCode(String terminalCode) {
        return repo.getBankIdByTerminalCode(terminalCode);
    }

    @Override
    public ITerminalInternalDTO getTerminalInternalDTOByMachineCode(String machineCode) {
        return repo.getTerminalInternalByMachineCode(machineCode);
    }

    @Override
    public ISubTerminalCodeDTO getSubTerminalCodeBySubTerminalCode(String terminalCode) {
        return repo.getSubTerminalCodeByTerminalCode(terminalCode);
    }

    @Override
    public String checkExistedRawTerminalCode(String code) {
        return repo.checkExistedRawTerminalCode(code);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveEntityByTerminalCode(String terminalCode) {
        return repo.getTerminalBankReceiveEntityByTerminalCode(terminalCode);
    }

    @Override
    public TerminalSubRawCodeDTO getTerminalSubFlow2ByTraceTransfer(String traceTransfer) {
        return repo.getTerminalSubFlow2ByTraceTransfer(traceTransfer);
    }

    @Override
    public TerminalSubRawCodeDTO getTerminalBankReceiveForRawByTerminalCode(String terminalCode) {
        return repo.getTerminalBankReceiveForRawByTerminalCode(terminalCode);
    }

    @Override
    public TerminalBankSyncDTO getTerminalBankReceive(String rawCode, String bankAccount, String bankCode) {
        return repo.getTerminalBankReceive(rawCode, bankAccount, bankCode);
    }

    @Override
    public void updateQrCodeTerminalSync(String data1, String data2, String traceTransfer, String id) {
        repo.updateQrCodeTerminalSync(data1, data2, traceTransfer, id);
    }

    @Override
    public TerminalBankReceiveEntity getTerminalBankReceiveEntityByRawTerminalCode(String subRawCode) {
        return repo.getTerminalBankReceiveEntityByRawTerminalCode(subRawCode);
    }

    @Override
    public ITerminalBankReceiveQR getTerminalBankReceiveQR(String subRawCode) {
        return repo.getTerminalBankReceiveQR(subRawCode);
    }
}