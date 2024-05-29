package com.vietqr.org.service;

import com.vietqr.org.dto.AccountCustomerMerchantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@FeignClient(name = "AccountCustomerService", url = "http://localhost:8084/")
public interface AccountCustomerService {

    public int insert(AccountCustomerEntity entity);

    public String getAccessKey(String password);

    String getAccessKeyByUsername(String username);

    @GetMapping("admin/account/merchant/{pw}")
    public List<AccountCustomerMerchantDTO> getMerchantNameByPassword(@PathVariable String pw);

    public String getAccountCustomerIdByUsername(String username);

}
