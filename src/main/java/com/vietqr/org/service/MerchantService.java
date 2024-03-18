package com.vietqr.org.service;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.dto.IStatisticMerchantDTO;
import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.dto.MerchantWebResponseDTO;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantService {
    int insertMerchant(MerchantEntity merchantEntity);

    List<MerchantResponseDTO> getMerchantsByUserId(String userId);

    MerchantWebResponseDTO getMerchantWebResponseDTO(String merchantId);

    MerchantWebResponseDTO getMerchantByUserIdLimit(String userId);

    IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId, String fromDate, String toDate);

    int inactiveMerchantByMerchantId(String merchantId, String userId);
}
