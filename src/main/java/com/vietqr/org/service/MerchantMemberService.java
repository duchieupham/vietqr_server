package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantMemberDTO;
import com.vietqr.org.dto.IMerchantMemberDetailDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantMemberService {

    String checkUserExistedFromMerchant(String merchantId, String id);

    int insert(MerchantMemberEntity entity);

    void removeMemberFromMerchant(String merchantId, String userId);

    void insertAll(List<MerchantMemberEntity> entity);

    List<IMerchantMemberDTO> findMerchantMemberByMerchantId(String merchantId, String value, int page, int size);

    IMerchantMemberDetailDTO getUserExistedFromMerchant(String merchantId, String userId);

    void deleteMerchantMemberByUserIdAndMerchantId(String merchantId, String userId);

    int countMerchantMemberByMerchantId(String merchantId);
}
