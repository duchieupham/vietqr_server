package com.vietqr.org.service;

import com.vietqr.org.dto.ISubTerminalDTO;
import com.vietqr.org.dto.ISubTerminalResponseDTO;
import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TerminalBankReceiveService {
    void insertAllTerminalBankReceive(List<TerminalBankReceiveEntity> terminalBankReceiveEntities);

    void insert(TerminalBankReceiveEntity terminalBankReceiveEntity);

    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalIdAndBankId(String terminalId, String bankId);

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
}
