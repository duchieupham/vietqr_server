package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.dto.bidv.CustomerVaItemDTO;
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
    public CustomerVaEntity getCustomerVaInfoById(String id) {
        return repo.getCustomerVaInfoById(id);
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

    @Override
    public String checkExistedLinkedBankAccount(String bankAccount) {
        return repo.checkExistedLinkedBankAccount(bankAccount);
    }

    @Override
    public List<CustomerVaItemDTO> getCustomerVasByUserId(String userId) {
        return repo.getCustomerVasByUserId(userId);
    }

}
