package com.vietqr.org.service;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import com.vietqr.org.repository.MerchantMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantMemberServiceImpl implements MerchantMemberService {

    @Autowired
    private MerchantMemberRepository repo;

    @Override
    public List<AccountMemberDTO> getMerchantMembersByUserId(String merchantId) {
        return repo.getMerchantMembersByUserId(merchantId);
    }

    @Override
    public String checkUserExistedFromMerchant(String merchantId, String id) {
        return repo.checkUserExistedFromMerchant(merchantId, id);
    }

    @Override
    public int insertMemberToMerchant(MerchantMemberEntity entity) {
        return repo.save(entity) != null ? 1 : 0;
    }

    @Override
    public void removeMemberFromMerchant(String merchantId, String userId) {
        repo.removeMemberFromMerchant(merchantId, userId);
    }
}
