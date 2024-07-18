package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantRoleRawDTO;
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

    String checkMemberHaveRole(String userId, String rolesAccept);

    List<IMerchantRoleRawDTO> getMerchantIdsByMerchantMemberIds(List<String> merchantMemberIds);

    List<String> getListUserIdRoles(String bankId, String roles);

    List<String> getRoleByUserIdAndBankId(String userId, String bankId);
}
