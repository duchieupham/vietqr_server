package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AnnualFeeMerchantDTO;
import com.vietqr.org.dto.CusSyncApiInfoDTO;
import com.vietqr.org.dto.CusSyncEcInfoDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.dto.MerchantServiceItemDTO;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.repository.CustomerSyncRepository;

@Service
public class CustomerSyncServiceImpl implements CustomerSyncService {

    @Autowired
    CustomerSyncRepository repo;

    @Override
    public int insertCustomerSync(CustomerSyncEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<CustomerSyncEntity> getCustomerSyncEntities() {
        return repo.getCustomerSyncEntities();
    }

    @Override
    public CustomerSyncEntity getCustomerSyncById(String id) {
        return repo.getCustomerSyncById(id);
    }

    @Override
    public String checkExistedCustomerSync(String userId) {
        return repo.checkExistedCustomerSync(userId);
    }

    @Override
    public void updateCustomerSyncInformation(String information, String userId) {
        repo.updateCustomerSyncInformation(information, userId);
    }

    @Override
    public String checkExistedCustomerSyncByInformation(String information) {
        return repo.checkExistedCustomerSyncByInformation(information);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncList() {
        return repo.getCustomerSyncList();
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncListByMerchant(String value, int offset, int size) {
        return repo.getCustomerSyncListByMerchant(value, offset, size);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncListByMerchantByBankAccount(String value, int offset, int size) {
        return repo.getCustomerSyncListByMerchantByBankAccount(value, offset, size);
    }

    @Override
    public int countCustomerSyncListByMerchant(String value) {
        return repo.countCustomerSyncListByMerchant(value);
    }

    @Override
    public int countCustomerSyncListByMerchantByBankAccount(String value) {
        return repo.countCustomerSyncListByMerchantByBankAccount(value);
    }

    @Override
    public int countCustomerSyncAPIListByMerchant(String value) {
        return repo.countCustomerSyncAPIListByMerchant(value);
    }

    @Override
    public int countCustomerSyncAPIListByMerchantByBankAccount(String value) {
        return repo.countCustomerSyncAPIListByMerchantByBankAccount(value);
    }

    @Override
    public int countCustomerSyncEcListByMerchant(String value) {
        return repo.countCustomerSyncEcListByMerchant(value);
    }

    @Override
    public int countCustomerSyncEcListByMerchantByBankAccount(String value) {
        return repo.countCustomerSyncEcListByMerchantByBankAccount(value);
    }

    @Override
    public Integer checkCustomerSyncTypeById(String id) {
        return repo.checkCustomerSyncTypeById(id);
    }

    @Override
    public CusSyncApiInfoDTO getCustomerSyncApiInfo(String id) {
        return repo.getCustomerSyncApiInfo(id);
    }

    @Override
    public CusSyncEcInfoDTO getCustomerSyncEcInfo(String id) {
        return repo.getCustomerSyncEcInfo(id);
    }

    @Override
    public void updateCustomerSyncStatus(boolean active, String customerSyncId) {
        repo.updateCustomerSyncStatus(active, customerSyncId);
    }

    @Override
    public List<String> checkExistedMerchant(String merchant) {
        return repo.checkExistedMerchant(merchant);
    }

    @Override
    public void updateCustomerSync(String url, String ip, String password, String port, String suffix,
                                   String username, String customerSyncId) {
        repo.updateCustomerSync(url, ip, password, port, suffix, username, customerSyncId);
    }

    @Override
    public List<AnnualFeeMerchantDTO> getMerchantForServiceFee() {
        return repo.getMerchantForServiceFee();
    }

    @Override
    public List<MerchantServiceItemDTO> getMerchantsMappingService() {
        return repo.getMerchantsMappingService();
    }

    @Override
    public List<CustomerSyncEntity> getCustomerSyncByAccountId(String accountId) {
        return repo.getCustomerSyncByAccountId(accountId);
    }

    @Override
    public List<AnnualFeeMerchantDTO> getMerchantForServiceFeeById(String customerSyncId) {
        return repo.getMerchantForServiceFeeById(customerSyncId);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncAPIList() {
        return repo.getCustomerSyncAPIList();
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchant(String value, int offset, int size) {
        return repo.getCustomerSyncAPIListByMerchant(value, offset, size);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchantByBankAccount(String value, int offset, int size) {
        return repo.getCustomerSyncAPIListByMerchantByBankAccount(value, offset, size);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncEcList() {
        return repo.getCustomerSyncEcList();
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncEcListByMerchant(String value, int offset, int size) {
        return repo.getCustomerSyncEcListByMerchant(value, offset, size);
    }

    @Override
    public List<CustomerSyncListDTO> getCustomerSyncEcListByMerchantByBankAccount(String value, int offset, int size) {
        return repo.getCustomerSyncEcListByMerchantByBankAccount(value, offset, size);
    }


    @Override
    public String getMerchantNameById(String id) {
        return repo.getMerchantNameById(id);
    }

    @Override
    public String getCustomerAddressById(String id) {
        return repo.getCustomerAddressById(id);
    }

    @Override
    public String getCustomerSyncByBankId(String bankId) {
        return repo.getCustomerSyncByBankId(bankId);
    }

    @Override
    public Integer getCountingCustomerSync() {
        return repo.getCountingCustomerSync();
    }

    @Override
    public String checkExistedMerchantName(String merchantName) {
        return repo.checkExistedMerchantName(merchantName);
    }

    @Override
    public void deleteCustomerSync(String id) {
        repo.deleteRelatedAccountCustomerBankByCustomerSyncId(id);
        repo.deleteRelatedAccountCustomerByCustomerSyncId(id);
        repo.deleteCustomerSyncById(id);
    }

    // @Override
    // public List<String> checkExistedCustomerSyncByUsername(String username) {
    // return repo.checkExistedCustomerSyncByUsername(username);
    // }

}
