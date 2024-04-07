package com.vietqr.org.service;

import com.vietqr.org.dto.KeyActiveBankCheckDTO;
import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import com.vietqr.org.entity.KeyActiveBankReceiveEntity;
import com.vietqr.org.repository.KeyActiveBankReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyActiveBankReceiveServiceImpl implements KeyActiveBankReceiveService {

    @Autowired
    private KeyActiveBankReceiveRepository repo;
    @Override
    public KeyActiveBankReceiveDTO checkKeyExist(String keyActive) {
        return repo.checkKeyExist(keyActive);
    }

    @Override
    public KeyActiveBankCheckDTO checkKeyActiveByKey(String keyActive) {
        return repo.checkKeyActiveByKey(keyActive);
    }

    @Override
    public int updateActiveKey(String keyActive, int version, int newVersion) {
        return repo.updateActiveKey(keyActive, version, newVersion);
    }

    @Override
    public List<String> checkDuplicatedKeyActives(List<String> keys) {
        return repo.checkDuplicatedKeyActives(keys);
    }

    @Override
    public void insertAll(List<KeyActiveBankReceiveEntity> entities) {
        repo.saveAll(entities);
    }
}
