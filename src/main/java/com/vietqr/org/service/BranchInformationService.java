package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.dto.BranchChoiceDTO;
import com.vietqr.org.dto.BranchConnectedCheckDTO;
import com.vietqr.org.dto.BranchFilterResponseDTO;

@Service
public interface BranchInformationService {

    public int insertBranchInformation(BranchInformationEntity entity);

    public List<BranchInformationEntity> getListBranchByBusinessId(String businessId);

    public void deleteBranch(String id);

    public void updateActiveBranch(boolean isActive, String id);

    public List<String> getBranchIdsByUserIdBusiness(String userId);

    public List<BranchChoiceDTO> getBranchsByBusinessId(String businessId);

    public List<BranchChoiceDTO> getValidBranchsByBusinessId(String businessId);

    public BranchInformationEntity getBranchById(String id);

    public List<String> getBranchIdsByBankId(String bankId);

    public List<BranchFilterResponseDTO> getBranchFilters(String businessId);

    public List<String> getBranchIdsByBusinessId(String businessId);

    public List<BranchFilterResponseDTO> getBranchFilterByUserIdAndRole(String userId, int role, String businessId);

    public List<BranchConnectedCheckDTO> getBranchContects(String businessId);

    public void deleteAllBranchByBusinessId(String businessId);
}
