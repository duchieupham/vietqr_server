package com.vietqr.org.service;

import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import com.vietqr.org.repository.KeyBankAccountReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyBankAccountReceiveServiceImpl implements KeyBankAccountReceiveService {

    @Autowired
    private KeyBankAccountReceiveRepository repo;
    @Override
    public KeyActiveBankReceiveDTO checkKeyExist(String key) {
        return repo.checkKeyExist(key);
    }
}
