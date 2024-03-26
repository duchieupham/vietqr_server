package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantMemberRoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MerchantMemberRoleService {
    void insert(MerchantMemberRoleEntity entity);

    void insertAll(List<MerchantMemberRoleEntity> entity);

    void removeMerchantMemberRole(String merchantMemberId, String userId);

    void deleteMerchantMemberRoleByUserIdAndMerchantId(String merchantId, String userId);

    void deleteMerchantMemberRoleByUserIdAndTerminalId(String terminalId, String userId);
}
