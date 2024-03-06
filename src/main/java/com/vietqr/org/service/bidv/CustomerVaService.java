package com.vietqr.org.service.bidv;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Service
public interface CustomerVaService {

    public int insert(CustomerVaEntity entity);

    public Long getCustomerVaLength();

    public CustomerVaEntity getCustomerVaInfoByBankId(String bankId);

    public void removeCustomerVa(String userId, String merchantId);

    public CustomerVaInfoDataDTO getCustomerVaInfo(String customerId);

    public String checkExistedCustomerId(String customerId);
}
