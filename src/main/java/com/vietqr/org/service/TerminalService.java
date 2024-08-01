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

    List<ITerminalShareDTO> getTerminalSharesByBankIds2(List<String> bankIds, String userId);

    List<TerminalResponseInterfaceDTO> getTerminalSharesByUserId(String userId, int offset);

    int countNumberOfTerminalShareByUserId(String userId);

    TerminalEntity findTerminalById(String id);

    List<TerminalResponseInterfaceDTO> getTerminalsByUserIdAndBankId(String userId, String bankId, int offset);

    List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankId(String userId, String bankId);

    int countNumberOfTerminalByUserIdAndBankId(String userId, String bankId);

    List<String> getUserIdsByTerminalCode(String terminalCode);

    TerminalEntity getTerminalByTerminalCode(String terminalCode, String bankAccount);

    TerminalEntity getTerminalByTerminalCode(String terminalCode);

    String getTerminalByTraceTransfer(String traceTransfer);

    List<ITerminalDetailWebDTO> getTerminalByUserId(String userId, int offset, String value);

    ITerminalBankResponseDTO getTerminalResponseById(String terminalId, String userId);

    ITerminalWebResponseDTO getTerminalWebById(String terminalId);

    void insertAllTerminal(List<TerminalEntity> terminalEntities);

    String checkExistedRawTerminalCode(String terminalCode);

    List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndUserId(String userId, String bankId);

    String getTerminalCodeByTerminalCode(String value);

    TerminalEntity getTerminalByTerminalBankReceiveCode(String terminalCode);

    int countNumberOfTerminalByUserIdOwner(String userId);

    List<TerminalMapperDTO> getTerminalsByUserIdAndMerchantId(String userId, String merchantId);

    List<TerminalMapperDTO> getTerminalsByUserIdAndMerchantIdOwner(String userId, String merchantId);

    List<String> getAllCodeByMerchantId(String merchantId, String userId);

    List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantId(String merchantId, String userId, int offset);

    List<String> getAllCodeByMerchantIdOwner(String merchantId, String userId);

    List<String> getAllCodeByMerchantIdIn(String merchantId, String userId);

    List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankIdOwner(String userId, String bankId);

    List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantIdOwner(String merchantId, String userId, int offset);

    List<ITerminalExportDTO> getTerminalExportByUserId(String userId);

    List<ITerminalExportDTO> getTerminalExportByCode(String terminalCode);

    List<ITerminalExportDTO> getTerminalByUserIdHaveRole(String userId);

    String getUserIdByTerminalId(String terminalId);

    String checkExistedTerminalRawCode(String code);

    TerminalEntity getTerminalByTerminalId(String terminalId);

    List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndMerchantId(String terminalId, String bankId);

    List<ITerminalSyncDTO> getTerminalByMidSync(String mid, int offset, int size);

    int countTerminalByMidSync(String midForSearch);

    List<String> checkExistedTerminalRawCodes(List<String> terminalCodes);

    String checkExistedPublishId(String publishId);

    List<String> getAllCodeByNameAndBankId(String name, String bankId);

    List<TerminalBankV2DTO> getTerminalByUserIdAndMerchantIds(String userId, List<String> merchantIds);

    List<String> getTerminalByUserIdAndMerchantId(String userId, String merchantId);
}
