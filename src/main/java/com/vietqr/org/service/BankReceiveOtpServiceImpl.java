package com.vietqr.org.service;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import com.vietqr.org.entity.BankReceiveOtpEntity;
import com.vietqr.org.repository.BankReceiveOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankReceiveOtpServiceImpl implements BankReceiveOtpService {
    @Autowired
    private BankReceiveOtpRepository repo;

    @Override
    public BankReceiveOtpDTO checkBankReceiveOtp(String userId, String bankId, String otp, String keyActive) {
        return repo.checkBankReceiveOtp(userId, bankId, otp, keyActive);
    }

    @Override
    public int insert(BankReceiveOtpEntity entity) {
        return repo.save(entity) != null ? 1 : 0;
    }

    @Override
    public BankReceiveOtpEntity getBankReceiveOtpByKey(String keyActive, String bankId, String userId) {
        return repo.getBankReceiveOtpByKey(keyActive, bankId, userId);
    }

    @Override
    public void updateStatusBankReceiveOtp(String id, int status) {
        repo.updateStatusBankReceiveOtp(id, status);
    }
}
