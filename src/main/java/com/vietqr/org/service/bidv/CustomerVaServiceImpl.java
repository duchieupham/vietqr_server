package com.vietqr.org.service.bidv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;
import com.vietqr.org.repository.CustomerVaRepository;

@Service
public class CustomerVaServiceImpl implements CustomerVaService {

    @Autowired
    CustomerVaRepository repo;

    @Override
    public int insert(CustomerVaEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public Long getCustomerVaLength() {
        return repo.getCustomerVaLength();
    }

    @Override
    public CustomerVaEntity getCustomerVaInfoByBankId(String bankId) {
        return repo.getCustomerVaInfoByBankId(bankId);
    }

    @Override
    public void removeCustomerVa(String userId, String merchantId) {
        repo.removeCustomerVa(userId, merchantId);
    }

    @Override
    public CustomerVaInfoDataDTO getCustomerVaInfo(String customerId) {
        return repo.getCustomerVaInfo(customerId);
    }

    @Override
    public String checkExistedCustomerId(String customerId) {
        return repo.checkExistedCustomerId(customerId);
    }

}
