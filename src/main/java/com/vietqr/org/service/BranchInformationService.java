package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.dto.BranchChoiceDTO;
import com.vietqr.org.dto.BranchFilterResponseDTO;

@Service
public interface BranchInformationService {

    int insertBranchInformation(BranchInformationEntity entity);

    List<BranchInformationEntity> getListBranchByBusinessId(String businessId);

    void deleteBranch(String id);

    void updateActiveBranch(boolean isActive, String id);

    List<String> getBranchIdsByUserIdBusiness(String userId);

    List<BranchChoiceDTO> getBranchsByBusinessId(String businessId);

    BranchInformationEntity getBranchById(String id);

    List<String> getBranchIdsByBankId(String bankId);

    public List<BranchFilterResponseDTO> getBranchFilters(String businessId);

    public List<String> getBranchIdsByBusinessId(String businessId);

    public List<BranchFilterResponseDTO> getBranchFilterByUserIdAndRole(String userId, int role, String businessId);

}
