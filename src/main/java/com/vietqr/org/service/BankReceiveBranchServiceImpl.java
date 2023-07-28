package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.BankReceiveBranchRepository;
import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.dto.AccountBankBranchDTO;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;

@Service
public class BankReceiveBranchServiceImpl implements BankReceiveBranchService {

    @Autowired
    BankReceiveBranchRepository repo;

    @Override
    public int insertBankReceiveBranch(BankReceiveBranchEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void deleteBankReceiveBranch(String id) {
        repo.deleteBankReceiveBranch(id);
    }

    @Override
    public List<AccountBankReceivePersonalDTO> getBankReceiveBranchs(String branchId) {
        return repo.getBankReceiveBranchs(branchId);
    }

    @Override
    public void deleteBankReceiveBranchByBankId(String bankId) {
        repo.deleteBankReceiveBranchByBankId(bankId);
    }

    @Override
    public BankReceiveBranchEntity getBankReceiveBranchByBankId(String bankId) {
        return repo.getBankReceiveBranchByBankId(bankId);
    }

    @Override
    public List<AccountBankBranchDTO> getBanksByBranchId(String branchId) {
        return repo.getBanksByBranchId(branchId);
    }

    @Override
    public void deleteBankReceiveBranchByBankIdAndBranchId(String bankId, String branchId) {
        repo.deleteBankReceiveBranchByBankIdAndBranchId(bankId, branchId);
    }

    @Override
    public List<String> getBankIdsByBusinessId(String businessId) {
        return repo.getBankIdsByBusinessId(businessId);
    }

    @Override
    public void deleteBankReceiveBranchByBusinessId(String businessId) {
        repo.deleteBankReceiveBranchByBusinessId(businessId);
    }

    @Override
    public List<AccountBankBranchDTO> getBanksByBusinessId(String businessId) {
        return repo.getBanksByBusinessId(businessId);
    }
}
