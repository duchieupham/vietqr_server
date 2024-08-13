package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantSyncV2DTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.repository.MerchantSyncV2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantSyncV2ServiceImpl implements MerchantSyncV2Service {

    @Autowired
    MerchantSyncV2Repository merchantSyncV2Repository;

    @Override
    public int countMerchantSyncByName(String name) {
        return merchantSyncV2Repository.countMerchantByName(name);
    }

    @Override
    public int countMerchantSyncByPublishId(String publishId) {
        return merchantSyncV2Repository.countMerchantByPublishId(publishId);
    }

    @Override
    public MerchantSyncEntity createMerchantSync(MerchantSyncEntity entity) {
        return merchantSyncV2Repository.save(entity);
    }

    @Override
    public List<IMerchantSyncV2DTO> getMerchantSyncs(int index, int size) {
        return merchantSyncV2Repository.getMerchantSyncs(index, size);
    }

    @Override
    public Optional<MerchantSyncEntity> getMerchantSyncById(String id) {
        return merchantSyncV2Repository.findById(id);
    }

    @Override
    public void deleteMerchantSync(String id) {
        merchantSyncV2Repository.deleteById(id);
    }

    @Override
    public int countMerchantSync() {
        return merchantSyncV2Repository.countMerchant();
    }
}
