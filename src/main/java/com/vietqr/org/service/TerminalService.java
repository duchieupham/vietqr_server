package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalEntity;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

@Service
public interface TerminalService {
    public int insertTerminal(TerminalEntity entity);

    public ITerminalDetailResponseDTO getTerminalById(String id);

    public void removeTerminalById(String id);

    public String checkExistedTerminal(String code);

    List<TerminalResponseInterfaceDTO> getTerminalsByUserId(String userId, int offset);

    int countNumberOfTerminalByUserId(String userId);

    List<ITerminalShareDTO> getTerminalSharesByBankIds(List<String> bankIds, String userId);

    List<TerminalResponseInterfaceDTO> getTerminalSharesByUserId(String userId, int offset);

    int countNumberOfTerminalShareByUserId(String userId);

    TerminalEntity findTerminalById(String id);

    List<TerminalResponseInterfaceDTO> getTerminalsByUserIdAndBankId(String userId, String bankId, int offset);

    List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankId(String userId, String bankId);

    int countNumberOfTerminalByUserIdAndBankId(String userId, String bankId);

    List<String> getUserIdsByTerminalCode(String terminalCode);

    TerminalEntity getTerminalByTerminalCode(String terminalCode, String bankAccount);

    String getTerminalByTraceTransfer(String traceTransfer);

    List<TerminalEntity> getAllTerminalNoQRCode();

    List<ITerminalDetailWebDTO> getTerminalByUserId(String userId, int offset, String value);

    List<ITerminalDetailWebDTO> getTerminalByUserIdAndMerchantId(String merchantId, String userId, int offset, String value);

    ITerminalBankResponseDTO getTerminalResponseById(String terminalId, String userId);

    ITerminalWebResponseDTO getTerminalWebById(String terminalId);

    String checkExistedTerminalIntoMerchant(String terminalId, String merchantId);

    TerminalEntity findTerminalByPublicId(String terminalId);

    void insertAllTerminal(List<TerminalEntity> terminalEntities);

    List<ITerminalTidResponseDTO> getTerminalByMerchantId(String merchantId, int offset, int size);

    String checkExistedRawTerminalCode(String terminalCode);

    List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndUserId(String userId, String bankId);
}
