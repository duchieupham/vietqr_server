package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.BranchMemberRepository;
import com.vietqr.org.entity.BranchMemberEntity;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessItemDTO;

@Service
public class BranchMemberServiceImpl implements BranchMemberService {

    @Autowired
    BranchMemberRepository repo;

    @Override
    public int insertBranchMember(BranchMemberEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<String> getBranchIdsByUserId(String userId) {
        return repo.getBranchIdsByUserId(userId);
    }

    @Override
    public List<MemberDTO> getBranchMembers(String branchId) {
        return repo.getBranchMembersByBranchId(branchId);
    }

    @Override
    public void deleteBranchMember(String id) {
        repo.deleteMemberFromBranch(id);
    }

    @Override
    public List<BusinessItemDTO> getBusinessItemByUserId(String userId) {
        return repo.getBusinessItemByUserId(userId);
    }

    @Override
    public List<String> getUserIdsByBusinessIdAndBranchId(String businessId, String branchId) {
        return repo.getUserIdsByBusinessIdAndBranchId(businessId, branchId);
    }

    @Override
    public int getTotalMemberInBranch(String branchId) {
        return repo.getTotalMemberInBranch(branchId);
    }

    @Override
    public MemberDTO getManagerByBranchId(String branchId) {
        return repo.getManagerByBranchId(branchId);
    }

    @Override
    public int getRoleFromBranch(String userId, String branchId) {
        int result = 0;
        try {
            result = Integer.parseInt(repo.getRoleFromBranch(userId, branchId) + "");
        } catch (Exception e) {
        }
        return result;
    }
}
