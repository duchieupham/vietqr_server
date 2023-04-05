package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.entity.BranchMemberEntity;;

@Service
public interface BranchMemberService {

    public int insertBranchMember(BranchMemberEntity entity);

    public List<String> getBranchIdsByUserId(String userId);

    public List<MemberDTO> getBranchMembers(String branchId);

    public void deleteBranchMember(String id);

    public List<BusinessItemDTO> getBusinessItemByUserId(String userId);

    public List<String> getUserIdsByBusinessIdAndBranchId(String businessId, String branchId);

    public int getTotalMemberInBranch(String branchId);

    public MemberDTO getManagerByBranchId(String branchId);

    public int getRoleFromBranch(String userId, String branchId);
}
