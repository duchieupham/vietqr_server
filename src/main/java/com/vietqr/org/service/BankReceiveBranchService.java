package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;

import com.vietqr.org.entity.BankReceiveBranchEntity;

@Service
public interface BankReceiveBranchService {

    public int insertBankReceiveBranch(BankReceiveBranchEntity entity);

    public void deleteBankReceiveBranch(String id);

    public List<AccountBankReceivePersonalDTO> getBankReceiveBranchs(String businessId);
}
