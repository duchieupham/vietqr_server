package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalEntity;
import com.vietqr.org.repository.TerminalRepository;
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
        return repo.getTerminalsShareByUserId(userId, offset);
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
        return repo.getTerminalsByUserIdAndBankId(userId, bankId, offset);
    }

    @Override
    public int countNumberOfTerminalByUserIdAndBankId(String userId, String bankId) {
        return repo.countNumberOfTerminalByUserIdAndBankId(userId, bankId);
    }

}
