package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TerminalBankReceiveService {
    void insertAll(List<TerminalBankReceiveEntity> terminalBankReceiveEntities);

    void insert(TerminalBankReceiveEntity terminalBankReceiveEntity);

    String getTerminalByTraceTransfer(String traceTransfer);

    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalId(String terminalId);

    String checkExistedTerminalCode(String code);

    TerminalBankReceiveEntity getTerminalBankByTerminalId(String terminalId);

    String getTerminalCodeByRawTerminalCode(String value);

    List<String> getSubTerminalCodeByTerminalCode(String terminalCodeForSearch);

    TerminalBankReceiveEntity getTerminalBankReceiveByTraceTransfer(String traceTransfer);

    List<String> getTerminalCodeByMainTerminalCodeList(List<String> terminalCodeAccess);

    String getTerminalBankReceiveByTerminalCode(String terminalCode);

    TerminalBankReceiveEntity getTerminalBankReceiveByRawTerminalCode(String machineCode);

    List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId, int offset, int size, String value);

    ISubTerminalDTO getSubTerminalDetailBySubTerminalId(String subTerminalId);

    int countSubTerminalByTerminalId(String terminalId, String value);

    List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId);

    List<ISubTerminalResponseDTO> getListSubTerminalByTerId(String terminalId);

    List<TerminalBankReceiveDTO> getTerminalBankReceiveResponseByTerminalId(String terminalId);

    ITerminalBankResponseDTO getTerminalBanksByTerminalId(String terminalId);

    List<ITerminalBankResponseDTO> getTerminalBanksByTerminalIds(List<String> terminalIds);

    List<IBankShareResponseDTO> getTerminalBankByUserId(String userId, int offset);

    int countNumberOfBankShareByUserId(String userId);

    List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset);

    void removeTerminalBankReceiveByTerminalId(String terminalId);

    List<String> getTerminalCodeByUserIdAndBankId(String userId, String bankId);

    List<String> getTerminalCodeByUserIdAndBankIdNoTerminal(String userId, String bankId);

    String getBankIdByTerminalCode(String terminalCode);

    ITerminalInternalDTO getTerminalInternalDTOByMachineCode(String machineCode);

    ISubTerminalCodeDTO getSubTerminalCodeBySubTerminalCode(String terminalCode);

    String checkExistedRawTerminalCode(String code);

    TerminalBankReceiveEntity getTerminalBankReceiveEntityByTerminalCode(String terminalCode);

    TerminalSubRawCodeDTO getTerminalSubFlow2ByTraceTransfer(String traceTransfer);

    TerminalSubRawCodeDTO getTerminalBankReceiveForRawByTerminalCode(String terminalCode);

    TerminalBankSyncDTO getTerminalBankReceive(String rawCode, String bankAccount, String bankCode);

    void updateQrCodeTerminalSync(String data1, String data2, String traceTransfer, String id);

    TerminalBankReceiveEntity getTerminalBankReceiveEntityByRawTerminalCode(String subRawCode);
}
