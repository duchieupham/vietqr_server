package com.vietqr.org.service;

import com.vietqr.org.dto.MerchantBankV2DTO;
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

    @Override
    public void save(MerchantBankReceiveEntity entity) {
        repo.save(entity);
    }

    @Override
    public MerchantBankReceiveEntity getMerchantBankByMerchantId(String merchantId, String bankId) {
        return repo.getMerchantBankByMerchantId(merchantId, bankId);
    }

    @Override
    public List<MerchantBankV2DTO> getMerchantBankV2ByBankId(String bankId, String userId, int offset, int size) {
        return repo.getMerchantBankV2ByBankId(bankId, userId, offset, size);
    }

    @Override
    public int countMerchantBankV2ByBankId(String bankId, String userId) {
        return repo.countMerchantBankV2ByBankId(bankId, userId);
    }

    @Override
    public void insert(MerchantBankReceiveEntity merchantBankReceiveEntity) {
        repo.save(merchantBankReceiveEntity);
    }
}
