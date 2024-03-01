package com.vietqr.org.service;

import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantService {
    int insertMerchant(MerchantEntity merchantEntity);

    List<MerchantResponseDTO> getMerchantsByUserId(String userId);

}
