package com.vietqr.org.service;

import com.vietqr.org.dto.KeyActiveBankCheckDTO;
import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import com.vietqr.org.entity.KeyActiveBankReceiveEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KeyActiveBankReceiveService {
    KeyActiveBankReceiveDTO checkKeyExist(String keyActive);

    KeyActiveBankCheckDTO checkKeyActiveByKey(String keyActive);

    int updateActiveKey(String keyActive, int version, int newVersion);

    List<String> checkDuplicatedKeyActives(List<String> keys);

    void insertAll(List<KeyActiveBankReceiveEntity> entities);

    List<KeyActiveBankReceiveEntity> getListKeyByBankId(String bankId);

    String getBankIdByKey(String key);

    Integer getStatusByKeyAndBankId(String key);
}
