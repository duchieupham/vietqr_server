package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.repository.AccountBankReceiveRepository;

@Service
public class AccountBankReceiveServiceImpl implements AccountBankReceiveService {

    @Autowired
    AccountBankReceiveRepository repo;

    @Override
    public int insertAccountBank(AccountBankReceiveEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void deleteAccountBank(String id) {
        repo.deleteAccountBank(id);
    }

    @Override
    public String checkExistedBank(String bankAccount, String bankTypeId) {
        return repo.checkExistedBankAccount(bankAccount, bankTypeId);
    }

    @Override
    public AccountBankReceiveEntity getAccountBankById(String bankId) {
        return repo.getAccountBankById(bankId);
    }

    @Override
    public String getCaiValueByBankId(String bankId) {
        return repo.getCaiValueByBankId(bankId);
    }

    @Override
    public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankAccountName,
            String bankAccount, String ewalletToken, String bankId) {
        repo.updateRegisterAuthenticationBank(nationalId, phoneAuthenticated, bankAccountName, bankAccount,
                ewalletToken, bankId);
    }

    // @Override
    // public AccountBankReceiveEntity getAccountBankByBankAccount(String
    // bankAccount) {
    // return repo.getAccountBankByBankAccount(bankAccount);
    // }

    @Override
    public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId) {
        return repo.getAccountBankByBankAccountAndBankTypeId(bankAccount, bankTypeId);
    }

    @Override
    public List<BusinessBankDTO> getBankByBranchId(String branchId) {
        return repo.getBankByBranchId(branchId);
    }

    @Override
    public void unRegisterAuthenticationBank(String bankAccount) {
        repo.unRegisterAuthenticationBank(bankAccount);
    }

    @Override
    public void updateStatusAccountBankByUserId(int status, String userId) {
        repo.updateStatusAccountBankByUserId(status, userId);
    }

    @Override
    public List<AccountBankConnectBranchDTO> getAccountBankConnect(String userId) {
        return repo.getAccountBankConnect(userId);
    }

    @Override
    public void updateBankType(String id, int type) {
        repo.updateBankType(id, type);
    }

    @Override
    public List<AccountBankWpDTO> getAccountBankReceiveWps(String userId) {
        return repo.getAccountBankReceiveWps(userId);
    }

    @Override
    public void updateSyncWp(String userId, String bankId) {
        repo.updateSyncWp(userId, bankId);
    }

    @Override
    public String getBankAccountById(String bankId) {
        return repo.getBankAccountById(bankId);
    }

    @Override
    public List<String> checkExistedBankAccountSameUser(String bankAccount, String bankTypeId, String userId) {
        return repo.checkExistedBankAccountSameUser(bankAccount, bankTypeId, userId);
    }

    @Override
    public String getUserIdByBankId(String bankId) {
        return repo.getUserIdByBankId(bankId);
    }

    @Override
    public List<AccountBankReceiveRPAItemDTO> getBankAccountsRPA(String userId) {
        return repo.getBankAccountsRPA(userId);
    }

    @Override
    public List<AccountBankReceiveByCusSyncDTO> getBankAccountsByCusSyncId(String customerSyncId, int offset) {
        return repo.getBankAccountsByCusSyncId(customerSyncId, offset);
    }

    @Override
    public String checkExistedBankAccountByBankAccount(String bankAccount) {
        return repo.checkExistedBankAccountByBankAccount(bankAccount);
    }

    @Override
    public void updateBankAccountSync(boolean sync, String id) {
        repo.updateBankAccountSync(sync, id);
    }

    @Override
    public String checkMMSBankAccount(String bankAccount) {
        return repo.checkMMSBankAccount(bankAccount);
    }

    @Override
    public void updateBankSync(boolean sync, String id) {
        repo.updateBankAccountSync(sync, id);
    }

    @Override
    public Boolean getAuthenticatedByBankId(String bankId) {
        return repo.getAuthenticatedByBankId(bankId);
    }

    @Override
    public void updateMMSActive(boolean sync, boolean mmsActive, String bankId) {
        repo.updateMMSActive(sync, mmsActive, bankId);
    }

    @Override
    public String getUserIdByBankAccountAuthenticated(String bankAccount) {
        return repo.getUserIdByBankAccountAuthenticated(bankAccount);
    }

    @Override
    public Boolean getMMSActiveByBankId(String bankId) {
        return repo.getMMSActiveByBankId(bankId);
    }

    @Override
    public AccountBankReceiveForNotiDTO findAccountBankIden(String bankAccount, String bankTypeId) {
        return repo.findAccountBankIden(bankAccount, bankTypeId);
    }

    @Override
    public String checkIsOwner(String bankId, String userId) {
        return repo.checkIsOwner(bankId, userId);
    }

    @Override
    public AccountBankReceiveShareForNotiDTO findAccountBankByTraceTransfer(String traceTransfer, String bankTypeId) {
        return repo.findAccountBankByTraceTransfer(traceTransfer, bankTypeId);
    }

    @Override
    public List<RegisterBankResponseDTO> getListBankByDate(String fromDate, String toDate, int offset) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
        return repo.getListBankByDate(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<RegisterBankResponseDTO> getListBankByDateAndPhone(String fromDate, String toDate, String value, int offset) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
        return repo.getListBankByDateAndPhone(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, value, offset);
    }

    @Override
    public List<RegisterBankResponseDTO> getListBankByDateAndBankTypeId(String fromDate, String toDate, String value, int offset) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
        return repo.getListBankByDateAndBankTypeId(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, value, offset);
    }

    @Override
    public List<RegisterBankResponseDTO> getListBankByDateAndBankAccount(String fromDate,
                                                                         String toDate,
                                                                         String value, int offset) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
        return repo.getListBankByDateAndBankAccount(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, value, offset);
    }

    @Override
    public List<RegisterBankResponseDTO> getListBankByDateAndBankName(String fromDate,
                                                                      String toDate,
                                                                      String value, int offset) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
        return repo.getListBankByDateAndBankName(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, value, offset);
    }


    @Override
    public List<String> getAllDate() {
        return repo.getAllDate();
    }

    @Override
    public SumOfBankDTO sumOfBankByStartDateEndDate(String date) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
        return repo.sumOfBankByStartDateEndDate(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<SumEachBankDTO> sumEachBank(String date) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
        return repo.sumEachBank(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

}
