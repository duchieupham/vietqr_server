package com.vietqr.org.service;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.dto.IStatisticMerchantDTO;
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

    @Override
    public IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId, String fromDate, String toDate) {
        return repo.getStatisticMerchantByMerchantAndUserId(merchantId, userId, fromDate, toDate);
    }

    @Override
    public int inactiveMerchantByMerchantId(String merchantId, String userId) {
        return repo.inactiveMerchantByMerchantId(merchantId, userId);
    }


}
