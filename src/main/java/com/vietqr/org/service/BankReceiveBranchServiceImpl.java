package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.BankReceiveBranchRepository;
import com.vietqr.org.entity.BankReceiveBranchEntity;
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
    public List<AccountBankReceivePersonalDTO> getBankReceiveBranchs(String businessId) {
        return repo.getBankReceiveBranchs(businessId);
    }
}
