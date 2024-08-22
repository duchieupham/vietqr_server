package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MerchantSyncService {
    List<IMerchantInvoiceDTO> getMerchantSyncs(int offset, int size);

    List<IMerchantInvoiceDTO> getMerchantSyncsByName(String value, int offset, int size);

    int countMerchantSyncsByName(String value);

    IMerchantEditDetailDTO getMerchantEditDetail(String merchantId);

    IMerchantInfoDTO getMerchantSyncInfo(String merchantId);

    List<IMerchantSyncDTO> getAllMerchants(String value, int offset, int size);

    IMerchantSyncDTO getMerchantById(String id);

    MerchantSyncEntity createMerchant(MerchantSyncEntity entity);

    MerchantSyncEntity updateMerchant(String id, MerchantSyncEntity entity);
    Optional<MerchantSyncEntity> findById(String id);

    void deleteMerchant(String id);

    int countMerchantsByName(String value);
    void savePlatformDetails(String platform, String userId, String details);

    void updateMerchantName(String midName, String mid);

    MerchantSyncEntity getMerchantSyncByName(String merchantName);

    MerchantSyncEntity getMerchantSyncByPublishId(String mid);

    MerchantSyncEntity getMerchantSyncById(String mid);

    int countMerchantByMidSync(String mid);

    List<IMerchantSyncPublicDTO> getMerchantByMidSync(String refId, int offset, int size);

    String getIdByPublicId(String publicId);

    String getMerchantIdSyncByName(String merchantName);

    String checkExistedPublishId(String code);

    void insertAll(List<MerchantSyncEntity> merchantSyncEntities);

    List<MerchantSyncEntity> findAllMerchants();
}
