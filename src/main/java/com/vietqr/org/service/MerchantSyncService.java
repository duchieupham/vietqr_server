package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantEditDetailDTO;
import com.vietqr.org.dto.IMerchantInfoDTO;
import com.vietqr.org.dto.IMerchantInvoiceDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantSyncService {
    List<IMerchantInvoiceDTO> getMerchantSyncs(int offset, int size);

    List<IMerchantInvoiceDTO> getMerchantSyncsByName(String value, int offset, int size);

    int countMerchantSyncsByName(String value);

    IMerchantEditDetailDTO getMerchantEditDetail(String merchantId);

    IMerchantInfoDTO getMerchantSyncInfo(String merchantId);
}
