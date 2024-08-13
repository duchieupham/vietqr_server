package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantSyncV2DTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MerchantSyncV2Service {

    int countMerchantSyncByName(String name);

    int countMerchantSyncByPublishId(String publishId);

    MerchantSyncEntity createMerchantSync(MerchantSyncEntity entity);

    List<IMerchantSyncV2DTO> getMerchantSyncs(int index, int size);

    Optional<MerchantSyncEntity> getMerchantSyncById(String id);

    void deleteMerchantSync(String id);

    int countMerchantSync();
}
