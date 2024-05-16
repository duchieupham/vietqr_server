package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantBankMapperDTO;
import org.springframework.stereotype.Service;

@Service
public interface BankReceiveConnectionService {
    IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId);
}
