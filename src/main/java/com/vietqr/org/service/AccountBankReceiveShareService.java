package com.vietqr.org.service;

import com.vietqr.org.dto.BankAccountActiveKeyResponseDTO;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountBankReceiveShareService {

    public int insertAccountBankReceiveShare(AccountBankReceiveShareEntity entity);

    public List<AccountBankReceiveShareDTO> getAccountBankReceiveShares(String userId);

    public List<IBankAccountActiveKeyResponseDTO> getAccountBankInfoActiveKey(String userId);

    public List<AccountMemberDTO> getMembersFromBankReceiveShare(String bankId);

    public void removeMemberFromBankReceiveShare(String userId, String bankId);

    public void removeAllMemberFromBankReceiveShare(String bankId);

    public String checkUserExistedFromBankReceiveShare(String bankId, String id);

    public void deleteAccountBankReceiveShareByBankId(String bankId);

    public void removeTerminalGroupByTerminalId(String terminalId);

    public void removeMemberFromTerminal(String terminalId, String userId);

    public String checkUserExistedFromTerminal(String terminalId, String userId);

    public List<AccountMemberDTO> getMembersFromTerminalId(String terminalId);

    public void removeBankAccountFromTerminal(String terminalId, String bankId);

    List<ITerminalBankResponseDTO> getTerminalBanksByTerminalIds(List<String> terminalIds);

    List<IBankShareResponseDTO> getTerminalBankByUserId(String userId, int offset);

    int countNumberOfBankShareByUserId(String userId);

    List<BankQRTerminalDTO> getBankIdsFromTerminalId(String terminalId);

    int insertAccountBankReceiveShare(List<AccountBankReceiveShareEntity> entities);

    List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset);

    int countNumberOfTerminalBankShareByUserId(String userId);

    List<String> getUserIdsFromTerminalId(String terminalId, String userId);

    List<TerminalBankReceiveDTO> getAccountBankReceiveShareByTerminalId(String userId, String terminalId);

    List<AccountBankReceiveShareEntity> getAccountBankReceiveShareByTerminalId(String terminalId);

    int countBankAccountByTerminalId(String terminalId);

    List<IAccountTerminalMemberDTO> getMembersWebByTerminalId(String terminalId, int offset);

    int countMembersByTerminalId(String terminalId);

    List<String> checkUserExistedFromBankId(String userId, String value);

    String checkUserExistedFromBankByTerminalCode(String value, String userId);

    String checkUserExistedFromBankAccountAndIsOwner(String userId, String bankId);

    ITerminalBankResponseDTO getTerminalBanksByTerminalId(String terminalId);

    AccountBankReceiveShareEntity getAccountAlreadyShare(String terminalId, String userId);

    int updateAccountBankArrangement(String bankId, int index, String userId);

    List<String> getListBankIdSharedByUserId(String userId);
}
