package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantEditDetailDTO;
import com.vietqr.org.dto.IMerchantInfoDTO;
import com.vietqr.org.dto.IMerchantInvoiceDTO;
import com.vietqr.org.dto.IMerchantSyncDTO;
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
}
