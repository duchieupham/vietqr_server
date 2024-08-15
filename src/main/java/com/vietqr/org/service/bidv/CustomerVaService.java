package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.dto.bidv.CustomerVaItemDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Service
public interface CustomerVaService {

    public int insert(CustomerVaEntity entity);

    public Long getCustomerVaLength();

    public CustomerVaEntity getCustomerVaInfoById(String id);

    public void removeCustomerVa(String userId, String merchantId);

    public CustomerVaInfoDataDTO getCustomerVaInfo(String customerId);

    public String checkExistedCustomerId(String customerId);

    public String checkExistedLinkedBankAccount(String bankAccount);

    public List<CustomerVaItemDTO> getCustomerVasByUserId(String userId);

    public String getUserIdByCustomerId(String customerId);

    public String checkExistedMerchantId(String merchantid);

    String getVaNumberByBankId(String bankId);
}
