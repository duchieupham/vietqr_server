package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.CustomerSyncInformationDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
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

    public CustomerSyncInformationDTO getCustomerSyncInformationById(String id);

    // public List<String> checkExistedCustomerSyncByUsername(String username);
}
