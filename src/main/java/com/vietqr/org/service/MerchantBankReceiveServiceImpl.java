package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantBankReceiveEntity;
import com.vietqr.org.repository.MerchantBankReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantBankReceiveServiceImpl implements MerchantBankReceiveService {

    @Autowired
    MerchantBankReceiveRepository repo;

    @Override
    public String checkExistedBankAccountInOtherMerchant(String bankAccount, String merchantId) {
        return repo.checkExistedBankAccountInOtherMerchant(bankAccount, merchantId);
    }

    @Override
    public MerchantBankReceiveEntity getMerchantBankReceiveByMerchantAndBankId(String merchantId, String id) {
        return repo.getMerchantBankReceiveByMerchantAndBankId(merchantId, id);
    }

    @Override
    public void insertAllMerchantBankReceive(List<MerchantBankReceiveEntity> merchantBankReceiveEntities) {
        repo.saveAll(merchantBankReceiveEntities);
    }
}
