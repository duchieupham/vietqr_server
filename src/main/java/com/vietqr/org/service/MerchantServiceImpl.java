package com.vietqr.org.service;

import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.dto.MerchantWebResponseDTO;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository repo;

    @Override
    public int insertMerchant(MerchantEntity merchantEntity) {
        return repo.save(merchantEntity) != null ? 1 : 0;
    }

    @Override
    public List<MerchantResponseDTO> getMerchantsByUserId(String userId) {
        return repo.getMerchantsByUserId(userId);
    }

    @Override
    public MerchantWebResponseDTO getMerchantWebResponseDTO(String merchantId) {
        return repo.getMerchantWebResponseDTO(merchantId);
    }

    @Override
    public MerchantWebResponseDTO getMerchantByUserIdLimit(String userId) {
        return repo.getMerchantByUserIdLimit(userId);
    }


}
