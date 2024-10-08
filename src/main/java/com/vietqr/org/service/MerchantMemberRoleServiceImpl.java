package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantRoleRawDTO;
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

    @Override
    public void deleteMerchantMemberRoleByUserIdAndTerminalId(String terminalId, String userId) {
        repo.deleteMerchantMemberRoleByUserIdAndTerminalId(terminalId, userId);
    }

    @Override
    public String checkMemberHaveRole(String userId, String rolesAccept) {
        return repo.checkMemberHaveRole(userId, rolesAccept);
    }

    @Override
    public List<IMerchantRoleRawDTO> getMerchantIdsByMerchantMemberIds(List<String> merchantMemberIds) {
        return repo.getMerchantIdsByMerchantMemberIds(merchantMemberIds);
    }

    @Override
    public List<String> getListUserIdRoles(String userId, String roles) {
        return repo.getListUserIdRoles(userId, roles);
    }

    @Override
    public List<String> getRoleByUserIdAndBankId(String userId, String bankId) {
        return repo.getRoleByUserIdAndBankId(userId, bankId);
    }

    @Override
    public List<String> getRoleByUserIdAndBankId(String userId, List<String> bankIds) {
        return repo.getRoleByUserIdAndBankId(userId, bankIds);
    }
}
