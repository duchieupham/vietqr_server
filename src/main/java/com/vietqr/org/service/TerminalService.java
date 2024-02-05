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
}
