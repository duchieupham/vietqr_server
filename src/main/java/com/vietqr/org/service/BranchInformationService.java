package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.dto.BranchChoiceDTO;

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
}
