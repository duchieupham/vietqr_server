package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantMemberRoleEntity;
import com.vietqr.org.repository.MerchantMemberRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantMemberRoleServiceImpl implements MerchantMemberRoleService {

    @Autowired
    private MerchantMemberRoleRepository repo;
    @Override
    public void insert(MerchantMemberRoleEntity entity) {
        repo.save(entity);
    }

    @Override
    public void insertAll(List<MerchantMemberRoleEntity> entity) {
        repo.saveAll(entity);
    }

    @Override
    public void removeMerchantMemberRole(String merchantMemberId, String userId) {
        repo.removeMerchantMemberRole(merchantMemberId, userId);
    }

    @Override
    public void deleteMerchantMemberRoleByUserIdAndMerchantId(String merchantId, String userId) {
        repo.deleteMerchantMemberRoleByUserIdAndMerchantId(merchantId, userId);
    }
}
