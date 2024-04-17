package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantBankReceiveEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantBankReceiveService {
    String checkExistedBankAccountInOtherMerchant(String bankAccount, String merchantId);

    MerchantBankReceiveEntity getMerchantBankReceiveByMerchantAndBankId(String merchantId, String id);

    void insertAllMerchantBankReceive(List<MerchantBankReceiveEntity> merchantBankReceiveEntities);
    void save(MerchantBankReceiveEntity entity);

    String getBankIdReceiveByMerchant(String key);

    MerchantBankReceiveEntity getMerchantBankByMerchantId(String merchantId, String bankId);
}
