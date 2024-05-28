package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantService {
    int insertMerchant(MerchantEntity merchantEntity);

    List<MerchantResponseDTO> getMerchantsByUserId(String userId, int offset);

    MerchantWebResponseDTO getMerchantWebResponseDTO(String merchantId);

    MerchantWebResponseDTO getMerchantByUserIdLimit(String userId);

    IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId, String fromDate, String toDate);

    int inactiveMerchantByMerchantId(String merchantId, String userId);

    List<MerchantResponseDTO> getMerchantsByUserIdNoPaging(String userId, String bankId);

    List<MerchantResponseListDTO> getMerchantsByUserId(String userId);
}
