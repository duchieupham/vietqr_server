package com.vietqr.org.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.util.DateTimeUtil;
import com.vietqr.org.util.StringUtil;
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
    public AccountBankReceiveEntity getAccountBankReceiveByBankAccountAndBankCode(String bankAccount, String bankCode) {
        return repo.getAccountBankReceiveByBankAccountAndBankCode(bankAccount, bankCode);
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
    public List<IAccountBankMonthDTO> getBankAccountStatistics() {
        return repo.getBankAccountStatistics();
    }

    @Override
    public BankDetailTypeCaiValueDTO getBankAccountTypeDetail(String bankId) {
        return repo.getBankAccountTypeDetail(bankId);
    }


    @Override
    public int countAllBankAccounts() {
        return repo.countAllBankAccounts();
    }

    @Override
    public int countBankAccountsByAccount(String keyword) {
        return repo.countBankAccountsByAccount(keyword);
    }

    @Override
    public int countBankAccountsByAccountName(String keyword) {
        return repo.countBankAccountsByAccountName(keyword);
    }

    @Override
    public int countBankAccountsByPhoneAuthenticated(String keyword) {
        return repo.countBankAccountsByPhoneAuthenticated(keyword);
    }

    @Override
    public int countBankAccountsByNationalId(String keyword) {
        return repo.countBankAccountsByNationalId(keyword);
    }

    @Override
    public List<BankAccountResponseDTO> getAllBankAccount(int offset, int size) {
        List<IBankAccountResponseDTO> accounts = repo.getAllBankAccounts(offset, size);
        return convertAndSanitize(accounts);
    }

    @Override
    public void unRegisterAuthenBank(String bankAccount, String ewalletToken) {
        repo.unRegisterAuthenBank(bankAccount, ewalletToken);
    }
    @Override
    public List<AccountBankReceiveEntity> findBankAccountsByMerchantId(String merchantId) {
        return repo.findBankAccountsByMerchantId(merchantId);
    }

    @Override
    public String getBankAccountNameByBankAccount(String bankAccount) {
        return repo.getBankAccountNameByBankAccount(bankAccount);
    }

    @Override
    public List<BankAccountResponseDTO> getBankAccountsByValidFeeToAndIsValidService(int offset, int size) {
        long currentTime = System.currentTimeMillis() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long sevenDaysLater = currentTime + 7 * 24 * 60 * 60 * 1000L;

        // Lấy các danh sách tài khoản theo trạng thái: quá hạn, gần hết hạn, còn hạn, chưa đăng ký dịch vụ
        List<BankAccountResponseDTO> overdueAccounts = convertAndSanitize(repo.getOverdueBankAccounts(currentTime, offset, size));
        List<BankAccountResponseDTO> nearlyExpiredAccounts = convertAndSanitize(repo.getNearlyExpiredBankAccounts(currentTime, sevenDaysLater, offset, size));
        List<BankAccountResponseDTO> validAccounts = convertAndSanitize(repo.getValidBankAccounts(sevenDaysLater, offset, size));
        List<BankAccountResponseDTO> notRegisteredAccounts = convertAndSanitize(repo.getNotRegisteredBankAccounts(offset, size));

        // Tạo danh sách tổng hợp và thêm các tài khoản theo thứ tự yêu cầu
        List<BankAccountResponseDTO> allAccounts = new ArrayList<>();
        allAccounts.addAll(overdueAccounts); // Quá hạn trước
        allAccounts.addAll(nearlyExpiredAccounts); // Gần hết hạn
        allAccounts.addAll(validAccounts); // Còn hạn
        allAccounts.addAll(notRegisteredAccounts); // Chưa đăng ký dịch vụ

        return allAccounts;
    }

    @Override
    public int countBankAccountsByValidFeeToAndIsValidService() {
        long currentTime = System.currentTimeMillis() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long sevenDaysLater = currentTime + 7 * 24 * 60 * 60 * 1000L;

        int overdueCount = repo.countOverdueBankAccounts(currentTime);
        int nearlyExpiredCount = repo.countNearlyExpiredBankAccounts(currentTime, sevenDaysLater);
        int validCount = repo.countValidBankAccounts(sevenDaysLater);
        int notRegisteredCount = repo.countNotRegisteredBankAccounts();

        // Trả về tổng số lượng tài khoản
        return overdueCount + nearlyExpiredCount + validCount + notRegisteredCount;
    }



    @Override
    public List<BankAccountResponseDTO> getBankAccountsByTimeCreate(int offset, int size) {
        return convertAndSanitize(repo.getBankAccountsByTimeCreate(offset, size));
    }

    @Override
    public int countBankAccountsByTimeCreate() {
        return repo.countBankAccountsByTimeCreate();
    }

    @Override
    public IAdminExtraBankDTO getExtraBankDataForAllTime() {
        long currentTime = System.currentTimeMillis() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long sevenDaysLater = currentTime + 7 * 24 * 60 * 60 * 1000L;
        return repo.getExtraBankDataForAllTime(currentTime, sevenDaysLater);
    }

    @Override
    public List<BankAccountResponseDTO> getBankAccountsByValidFeeToAndIsValidServiceWithSearch(Integer searchType, String value, int offset, int size) {
        switch (searchType) {
            case 3:
                return getBankAccountsByAccounts(value, offset, size);
            case 4:
                return getBankAccountsByAccountNames(value, offset, size);
            case 5:
                return getBankAccountsByPhoneAuthenticated(value, offset, size);
            case 6:
                return getBankAccountsByNationalIds(value, offset, size);
            default:
                return getBankAccountsByValidFeeToAndIsValidService(offset, size);
        }
    }

    @Override
    public int countBankAccountsByValidFeeToAndIsValidServiceWithSearch(Integer searchType, String value) {
        switch (searchType) {
            case 3:
                return countBankAccountsByAccount(value);
            case 4:
                return countBankAccountsByAccountName(value);
            case 5:
                return countBankAccountsByPhoneAuthenticated(value);
            case 6:
                return countBankAccountsByNationalId(value);
            default:
                return countBankAccountsByValidFeeToAndIsValidService();
        }
    }

    @Override
    public List<BankAccountResponseDTO> getBankAccountsByTimeCreateWithSearch(Integer searchType, String value, int offset, int size) {
        switch (searchType) {
            case 3: // Tìm kiếm theo TKNH
                return getBankAccountsByAccounts(value, offset, size); // Sử dụng service có sẵn
            case 4: // Tìm kiếm theo Chủ TK
                return getBankAccountsByAccountNames(value, offset, size); // Sử dụng service có sẵn
            case 5: // Tìm kiếm theo SĐT
                return getBankAccountsByPhoneAuthenticated(value, offset, size); // Sử dụng service có sẵn
            case 6: // Tìm kiếm theo CMND
                return getBankAccountsByNationalIds(value, offset, size); // Sử dụng service có sẵn
            default: // Nếu không có searchType hoặc không tìm kiếm theo 3,4,5,6
                return getBankAccountsByTimeCreate(offset, size); // Lọc theo thời gian tạo gần đây
        }
    }

    @Override
    public int countBankAccountsByTimeCreateWithSearch(Integer searchType, String value) {
        switch (searchType) {
            case 3: // Tìm kiếm theo TKNH
                return countBankAccountsByAccount(value); // Sử dụng service có sẵn
            case 4: // Tìm kiếm theo Chủ TK
                return countBankAccountsByAccountName(value); // Sử dụng service có sẵn
            case 5: // Tìm kiếm theo SĐT
                return countBankAccountsByPhoneAuthenticated(value); // Sử dụng service có sẵn
            case 6: // Tìm kiếm theo CMND
                return countBankAccountsByNationalId(value); // Sử dụng service có sẵn
            default: // Nếu không có searchType hoặc không tìm kiếm theo 3,4,5,6
                return countBankAccountsByTimeCreate(); // Lọc theo thời gian tạo gần đây
        }
    }


    @Override
    public List<BankAccountResponseDTO> getBankAccountsByNationalIds(String keyword, int offset, int size) {
        List<IBankAccountResponseDTO> accounts = repo.getBankAccountsByNationalId(keyword, offset, size);
        return convertAndSanitize(accounts);
    }

    @Override
    public List<BankAccountResponseDTO> getBankAccountsByPhoneAuthenticated(String keyword, int offset, int size) {
        List<IBankAccountResponseDTO> accounts = repo.getBankAccountsByPhoneAuthenticated(keyword, offset, size);
        return convertAndSanitize(accounts);
    }
    @Override
    public List<BankAccountResponseDTO> getBankAccountsByAccountNames(String keyword, int offset, int size) {
        List<IBankAccountResponseDTO> accounts = repo.getBankAccountsByAccountName(keyword, offset, size);
        return convertAndSanitize(accounts);
    }
    @Override
    public List<BankAccountResponseDTO> getBankAccountsByAccounts(String keyword, int offset, int size) {
        List<IBankAccountResponseDTO> accounts = repo.getBankAccountsByAccount(keyword, offset, size);
        return convertAndSanitize(accounts);
    }

    private List<BankAccountResponseDTO> convertAndSanitize(List<IBankAccountResponseDTO> accounts) {
        return accounts.stream()
                .map(account -> new BankAccountResponseDTO(
                        account.getBankId()== null ? "" : account.getBankId(),
                        account.getBankAccount() == null ? "" : account.getBankAccount(),
                        account.getBankAccountName() == null ? "" : account.getBankAccountName(),
                        account.getBankShortName() == null ? "" : account.getBankShortName(),
                        account.getPhoneAuthenticated() == null ? "" : account.getPhoneAuthenticated(),
                        account.getMmsActive(),
                        account.getNationalId() == null ? "" : account.getNationalId(),
                        account.getValidFeeTo() == null ? 0 : account.getValidFeeTo(),
                        account.getValidFrom() == null ? 0 : account.getValidFrom(),
                        account.getTimeCreate() == null ? 0 : account.getTimeCreate(),
                        account.getPhoneNo() == null ? "" : account.getPhoneNo(),
                        account.getEmail() == null ? "" : account.getEmail(),
                        account.getStatus(),
                        account.getVso() == null ? "": account.getVso(),
                        StringUtil.getValueNullChecker(account.getIsValidService()),
                        StringUtil.getValueNullChecker(account.getIsAuthenticated()),
                        account.getBankTypeStatus(),
                        account.getBankCode()
                ))
                .collect(Collectors.toList());
    }



}
