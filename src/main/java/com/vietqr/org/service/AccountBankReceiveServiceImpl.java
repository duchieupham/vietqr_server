package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.repository.AccountBankReceiveRepository;

@Service
public class AccountBankReceiveServiceImpl implements AccountBankReceiveService {

    @Autowired
    AccountBankReceiveRepository repo;

    @Override
    public List<IBankShareDTO> getBankShareInfoByUserId(String userId) {
        return repo.getBankShareInfoByUserId(userId);
    }

    @Override
    public List<IBankInfoDTO> getBankInfoByUserId(String userId) {
        return repo.getBankInfoByUserId(userId);
    }

    @Override
    public List<IListAccountBankDTO> getListBankAccounts(String value, int offset, int size) {
        return repo.getListBankAccounts(value, offset, size);
    }

    @Override
    public int countListBankAccounts() {
        return repo.countListBankAccounts();
    }

    @Override
    public List<IAccountBankReceiveDTO> getBankIdsByUserId(String userId) {
        return repo.getBankIdsByUserId(userId);
    }

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
    public KeyBankReceiveActiveDTO getAccountBankKeyById(String bankId) {
        return repo.getAccountBankKeyById(bankId);
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
    public String checkExistedBankAccountByBankAccountAndBankCode(String bankAccount, String bankCode) {
        return repo.checkExistedBankAccountByBankAccountAndBankCode(bankAccount, bankCode);
    }

    @Override
    public String getBankShortNameByBankId(String bankId) {
        return repo.getBankShortNameByBankId(bankId);
    }

    @Override
    public AccountBankReceiveEntity checkExistedBankAccountAuthenticated(String bankAccount, String bankCode) {
        return repo.checkExistedBankAccountAuthenticated(bankAccount, bankCode);
    }

    @Override
    public String getBankNameByBankId(String bankTypeId) {
        return repo.getBankNameByBankId(bankTypeId);
    }

    @Override
    public List<TerminalBankReceiveDTO> getAccountBankReceiveByUseId(String userId) {
        return repo.getAccountBankReceiveByUseId(userId);
    }

    @Override
    public BankReceiveCheckDTO checkBankReceiveActive(String bankId) {
        return repo.checkBankReceiveActive(bankId);
    }

    @Override
    public int updateActiveBankReceive(String bankId, long validFeeFrom, long validFeeTo) {
        return repo.updateActiveBankReceive(bankId, validFeeFrom, validFeeTo);
    }

    @Override
    public boolean checkIsActiveService(String bankId) {
        return repo.checkIsActiveService(bankId);
    }

    @Override
    public IBankAccountInfoDTO getAccountBankInfoById(String bankId) {
        return repo.getAccountBankInfoById(bankId);
    }

    @Override
    public IAccountBankReceiveDTO getAccountBankInfoResById(String bankAccount, String bankCode) {
        return repo.getAccountBankInfoResById(bankAccount, bankCode);
    }

    @Override
    public IBankReceiveMapperDTO getMerchantBankMapper(String bankId) {
        return repo.getMerchantBankMapper(bankId);
    }

    @Override
    public List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId) {
        return repo.getCustomerDetailByBankId(bankId);
    }

    @Override
    public int countBankInvoiceByBankAccount(String value) {
        return repo.countBankInvoiceByBankAccount(value);
    }

    @Override
    public List<IBankAccountInvoiceInfoDTO> getBankInvoiceByBankAccount(String value, int offset, int size) {
        return repo.getBankInvoiceByBankAccount(value, offset, size);
    }

    @Override
    public AccountBankDetailAdminDTO getAccountBankDetailAdmin(String bankId) {
        return repo.getAccountBankDetailAdmin(bankId);
    }

    @Override
    public IBankAccountInvoicesDTO getBankAccountInvoices(String bankId) {
        return repo.getBankAccountInvoices(bankId);
    }

    @Override
    public IAccountBankReceiveDTO getAccountBankInfoResById(String bankId) {
        return repo.getAccountBankInfoResById(bankId);
    }

    @Override
    public BankAccountRechargeDTO getBankAccountRecharge(String bankId) {
        return repo.getBankAccountRecharge(bankId);
    }

    @Override
    public IBankReceiveFeePackageDTO getCustomerBankDetailByBankId(String bankId) {
        return repo.getCustomerBankDetailByBankId(bankId);
    }

    @Override
    public List<IAccountBankReceiveDTO> getBankIdsByBankId(String bankId) {
        return repo.getBankIdsByBankId(bankId);
    }

    @Override
    public AccountBankReceiveEntity getAccountBankByCustomerIdAndByServiceId(String customerId) {
        return repo.getAccountBankByCustomerIdAndByServiceId(customerId);
    }

    @Override
    public CustomerVaInfoDataDTO getAccountCustomerInfo(String customerId) {
        return repo.getAccountCustomerInfo(customerId);
    }

    @Override
    public void updateRegisterAuthentication(String userId, String bankId) {
        repo.updateRegisterAuthentication(userId, bankId);
    }

    @Override
    public String getBankIdByUserIdAndMerchantId(String userId, String merchantId) {
        return repo.getBankIdByUserIdAndMerchantId(userId, merchantId);
    }

    @Override
    public BidvUnlinkedDTO getMerchantIdByBankAccountBidvAuthen(String bankAccount, String bankCode) {
        return repo.getMerchantIdByBankAccountBidvAuthen(bankAccount, bankCode);
    }

    @Override
    public void updateRegisterUnlinkBidv(String userId, String bankId) {
        repo.updateRegisterUnlinkBidv(userId, bankId);
    }

    @Override
    public String getBankCodeByBankId(String bankId) {
        return repo.getBankCodeByBankId(bankId);
    }

    @Override
    public void updateRegisterAuthenticationBankBIDV(String nationalId, String phoneAuthenticated,
                                                     String bankAccountName, String bankAccount, String customerId,
                                                     String ewalletToken, String bankId) {
        repo.updateRegisterAuthenticationBankBIDV(nationalId, phoneAuthenticated, bankAccountName, bankAccount,
                customerId, ewalletToken, bankId);
    }

    @Override
    public String getBankIdByBankAccount(String bankAccount, String bankShortName) {
        return repo.getBankIdByBankAccount(bankAccount, bankShortName);
    }

    @Override
    public BankAccountAdminDTO getUserIdAndMidByBankId(String bankId) {
        return repo.getUserIdAndMidByBankId(bankId);
    }

    @Override
    public void updateVsoBankAccount(String vso, String bankId) {
        repo.updateVsoBankAccount(vso, bankId);
    }

    @Override
    public void unRegisterAuthenBank(String bankAccount, String ewalletToken) {
        repo.unRegisterAuthenBank(bankAccount, ewalletToken);
    }
}
