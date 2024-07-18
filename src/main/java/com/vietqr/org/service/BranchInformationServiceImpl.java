package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.repository.BranchInformationRepository;
import com.vietqr.org.dto.BranchChoiceDTO;
import com.vietqr.org.dto.BranchConnectedCheckDTO;
import com.vietqr.org.dto.BranchFilterResponseDTO;

@Service
public class BranchInformationServiceImpl implements BranchInformationService {

    @Autowired
    BranchInformationRepository repo;

    @Override
    public int insertBranchInformation(BranchInformationEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<BranchInformationEntity> getListBranchByBusinessId(String businessId) {
        return repo.getListBranchByBusinessId(businessId);
    }

    @Override
    public void deleteBranch(String id) {
        repo.deleteBranch(id);
    }

    @Override
    public void updateActiveBranch(boolean isActive, String id) {
        repo.updateActiveBranch(isActive, id);
    }

    @Override
    public List<String> getBranchIdsByUserIdBusiness(String userId) {
        return repo.getBranchIdsByUserIdBusiness(userId);
    }

    @Override
    public List<BranchChoiceDTO> getBranchsByBusinessId(String businessId) {
        return repo.getBranchsByBusinessId(businessId);
    }

    @Override
    public BranchInformationEntity getBranchById(String id) {
        return repo.getBranchById(id);
    }

    @Override
    public List<String> getBranchIdsByBankId(String bankId) {
        return repo.getBranchIdsByBankId(bankId);
    }

    @Override
    public List<BranchFilterResponseDTO> getBranchFilters(String businessId) {
        return repo.getBranchFilters(businessId);
    }

    @Override
    public List<String> getBranchIdsByBusinessId(String businessId) {
        return repo.getBranchIdsByBusinessId(businessId);
    }

    @Override
    public List<BranchFilterResponseDTO> getBranchFilterByUserIdAndRole(String userId, int role, String businessId) {
        return repo.getBranchFilterByUserIdAndRole(userId, role, businessId);
    }

    @Override
    public List<BranchConnectedCheckDTO> getBranchContects(String businessId) {
        return repo.getBranchContects(businessId);
    }

    @Override
    public void deleteAllBranchByBusinessId(String businessId) {
        repo.deleteAllBranchByBusinessId(businessId);
    }

    @Override
    public List<BranchChoiceDTO> getValidBranchsByBusinessId(String businessId) {
        return repo.getValidBranchsByBusinessId(businessId);
    }

}
