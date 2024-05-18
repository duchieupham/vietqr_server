package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantBankMapperDTO;
import com.vietqr.org.repository.BankReceiveConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankReceiveConnectionServiceImpl implements BankReceiveConnectionService {

    @Autowired
    private BankReceiveConnectionRepository repo;

    @Override
    public IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId) {
        return repo.getMerchantBankMapper(merchantId, bankId);
    }
}
