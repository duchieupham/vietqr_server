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

    public List<CustomerSyncListDTO> getCustomerSyncAPIList();

    public List<CustomerSyncListDTO> getCustomerSyncEcList();

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
}
