package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import com.vietqr.org.repository.AccountBankReceiveShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountBankReceiveShareServiceImpl implements AccountBankReceiveShareService {

    @Autowired
    private AccountBankReceiveShareRepository repo;

    @Override
    public int insertAccountBankReceiveShare(AccountBankReceiveShareEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }


    @Override
    public int insertAccountBankReceiveShare(List<AccountBankReceiveShareEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset) {
//        return repo.getTerminalBankShareByUserId(userId, offset);
        return repo.getTerminalBankShareByUserId(userId);
    }

    @Override
    public int countNumberOfTerminalBankShareByUserId(String userId) {
        return repo.countNumberOfTerminalBankShareByUserId(userId);
    }

    @Override
    public List<String> getUserIdsFromTerminalId(String terminalId, String userId) {
        return repo.getUserIdsFromTerminalId(terminalId, userId);
    }

    @Override
    public List<TerminalBankReceiveDTO> getAccountBankReceiveShareByTerminalId(String userId, String terminalId) {
        return repo.getAccountBankReceiveShareByTerminalId(userId, terminalId);
    }

    @Override
    public List<AccountBankReceiveShareEntity> getAccountBankReceiveShareByTerminalId(String terminalId) {
        return repo.getAccountBankReceiveShareByTerminalId(terminalId);
    }

    @Override
    public int countBankAccountByTerminalId(String terminalId) {
        return repo.countBankAccountByTerminalId(terminalId);
    }

    @Override
    public List<IAccountTerminalMemberDTO> getMembersWebByTerminalId(String terminalId, int offset) {
        return repo.getMembersWebByTerminalId(terminalId, offset);
    }

    @Override
    public int countMembersByTerminalId(String terminalId) {
        return repo.countMembersByTerminalId(terminalId);
    }

    @Override
    public List<String> checkUserExistedFromBankId(String userId, String value) {
        return repo.checkUserExistedFromBankId(userId, value);
    }

    @Override
    public String checkUserExistedFromBankByTerminalCode(String value, String userId) {
        return repo.checkUserExistedFromBankByTerminalCode(value, userId);
    }

    @Override
    public String checkUserExistedFromBankAccountAndIsOwner(String userId, String bankId) {
        return repo.checkUserExistedFromBankAccountAndIsOwner(userId, bankId);
    }

    @Override
    public List<AccountBankReceiveShareDTO> getAccountBankReceiveShares(String userId) {
        return repo.getAccountBankReceiveShare(userId);
    }

    @Override
    public List<AccountMemberDTO> getMembersFromBankReceiveShare(String bankId) {
        return repo.getMembersFromBank(bankId);
    }

    @Override
    public void removeMemberFromBankReceiveShare(String userId, String bankId) {
        repo.removeMemberFromBank(userId, bankId);
    }

    @Override
    public void removeAllMemberFromBankReceiveShare(String bankId) {
        repo.removeAllMemberFromBank(bankId);
    }

    @Override
    public String checkUserExistedFromBankReceiveShare(String bankId, String id) {
        return repo.checkUserExistedFromBank(bankId, id);
    }

    @Override
    public void deleteAccountBankReceiveShareByBankId(String bankId) {
        repo.deleteAccountBankReceiveShareByBankId(bankId);
    }

    @Override
    public void removeTerminalGroupByTerminalId(String terminalId) {
        repo.removeTerminalGroupByTerminalId(terminalId);
    }

    @Override
    public void removeMemberFromTerminal(String terminalId, String userId) {
        repo.removeMemberFromTerminal(terminalId, userId);
    }

    @Override
    public String checkUserExistedFromTerminal(String terminalId, String userId) {
        return repo.checkUserExistedFromTerminal(terminalId, userId);
    }

    @Override
    public List<AccountMemberDTO> getMembersFromTerminalId(String terminalId) {
        return repo.getMembersFromTerminal(terminalId);
    }

    @Override
    public void removeBankAccountFromTerminal(String terminalId, String bankId) {
        repo.removeBankAccountFromTerminal(terminalId, bankId);
    }

    @Override
    public List<ITerminalBankResponseDTO> getTerminalBanksByTerminalIds(List<String> terminalIds) {
        return repo.findByTerminalIdIn(terminalIds);
    }

    @Override
    public List<IBankShareResponseDTO> getTerminalBankByUserId(String userId, int offset) {
//        return repo.findBankShareByUserId(userId, offset);
        return repo.findBankShareByUserId(userId);
    }

    @Override
    public int countNumberOfBankShareByUserId(String userId) {
        return repo.countNumberOfBankShareByUserId(userId);
    }

    @Override
    public List<BankQRTerminalDTO> getBankIdsFromTerminalId(String terminalId) {
        return repo.getBankIdsFromTerminalId(terminalId);
    }

}
