package com.vietqr.org.service;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantMemberService {
    List<AccountMemberDTO> getMerchantMembersByUserId(String merchantId);

    String checkUserExistedFromMerchant(String merchantId, String id);

    int insertMemberToMerchant(MerchantMemberEntity entity);

    void removeMemberFromMerchant(String merchantId, String userId);
}
