package com.vietqr.org.service;

import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import org.springframework.stereotype.Service;

@Service
public interface KeyBankAccountReceiveService {
    KeyActiveBankReceiveDTO checkKeyExist(String key);
}
