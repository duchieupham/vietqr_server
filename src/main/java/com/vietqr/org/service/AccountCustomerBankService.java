package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankReceiveServiceItemDTO;
import com.vietqr.org.dto.AccountCustomerBankInfoDTO;
import com.vietqr.org.dto.AnnualFeeBankDTO;
import com.vietqr.org.entity.AccountCustomerBankEntity;

@Service
public interface AccountCustomerBankService {

    public int insert(AccountCustomerBankEntity entity);

    public List<AccountCustomerBankEntity> getAccountCustomerBankByBankId(String bankId);

    public String checkExistedAccountCustomerBank(String bankId, String customerSyncId);

    public List<String>         checkExistedCustomerSyncByUsername(String username);

    public List<String> checkExistedAccountCustomerBankByBankAccount(String bankAccount, String customerSyncId);

    public void removeBankAccountFromCustomerSync(String bankId, String customerSyncId);

    public List<String> getBankIdsByCustomerSyncId(String customerSyncId);

    public List<AnnualFeeBankDTO> getBanksAnnualFee(String customerSyncId);

    public List<AccountBankReceiveServiceItemDTO> getBankAccountsByMerchantId(String customerSyncId);

    public String getMerchantByBankId(String bankId);

    String checkExistedByBankIdAndCustomerSyncId(String bankId, String customerSyncId);

    AccountCustomerBankInfoDTO getBankSizeAndAddressByCustomerSyncId(String customerSyncId);

    String checkExistedBankAccountIntoMerchant(String bankAccount, String customerSyncId);

    AccountCustomerBankEntity getAccountCustomerBankByBankIdAndMerchantId(String bankId, String merchantId);
}
