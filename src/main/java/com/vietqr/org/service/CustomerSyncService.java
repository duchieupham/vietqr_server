package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AnnualFeeMerchantDTO;
import com.vietqr.org.dto.CusSyncApiInfoDTO;
import com.vietqr.org.dto.CusSyncEcInfoDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.dto.MerchantServiceItemDTO;
import com.vietqr.org.entity.CustomerSyncEntity;

@Service
public interface CustomerSyncService {

    public int insertCustomerSync(CustomerSyncEntity entity);

    public List<CustomerSyncEntity> getCustomerSyncEntities();

    public CustomerSyncEntity getCustomerSyncById(String id);

    public String checkExistedCustomerSync(String userId);

    public void updateCustomerSyncInformation(String information, String userId);

    public String checkExistedCustomerSyncByInformation(String information);

    public List<CustomerSyncListDTO> getCustomerSyncList();

    public List<CustomerSyncListDTO> getCustomerSyncListByMerchant(String value, int offset, int size);

    public List<CustomerSyncListDTO> getCustomerSyncListByMerchantByBankAccount(String value, int offset, int size);

    public int countCustomerSyncListByMerchant(String value);

    public int countCustomerSyncListByMerchantByBankAccount(String value);

    public int countCustomerSyncAPIListByMerchant(String value);

    public int countCustomerSyncAPIListByMerchantByBankAccount(String value);

    public int countCustomerSyncEcListByMerchant(String value);

    public int countCustomerSyncEcListByMerchantByBankAccount(String value);

    public List<CustomerSyncListDTO> getCustomerSyncAPIList();

    public List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchant(String value, int offset, int size);

    public List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchantByBankAccount(String value, int offset, int size);

    public List<CustomerSyncListDTO> getCustomerSyncEcList();

    public List<CustomerSyncListDTO> getCustomerSyncEcListByMerchant(String value, int offset, int size);

    public List<CustomerSyncListDTO> getCustomerSyncEcListByMerchantByBankAccount(String value, int offset, int size);

    // public List<String> checkExistedCustomerSyncByUsername(String username);

    // 0 => API Service
    // 1 => E-Commerce
    // get user type by id
    Integer checkCustomerSyncTypeById(String id);

    CusSyncApiInfoDTO getCustomerSyncApiInfo(String id);

    CusSyncEcInfoDTO getCustomerSyncEcInfo(String id);

    public void updateCustomerSyncStatus(boolean active, String customerSyncId);

    List<String> checkExistedMerchant(String merchant);

    public void updateCustomerSync(String url, String ip, String password, String port, String suffix,
                                   String username, String customerSyncId);

    List<AnnualFeeMerchantDTO> getMerchantForServiceFee();

    public List<MerchantServiceItemDTO> getMerchantsMappingService();

    public List<CustomerSyncEntity> getCustomerSyncByAccountId(String accountId);

    public List<AnnualFeeMerchantDTO> getMerchantForServiceFeeById(String customerSyncId);

    public String getMerchantNameById(String id);

    public String getCustomerAddressById(String id);

    public String getCustomerSyncByBankId(String bankId);

    public Integer getCountingCustomerSync();

    public String checkExistedMerchantName(String merchantName);
}
