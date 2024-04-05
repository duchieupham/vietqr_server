package com.vietqr.org.service;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import com.vietqr.org.repository.BankReceiveOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankReceiveOTPServiceImpl implements BankReceiveOTPService {
    @Autowired
    private BankReceiveOTPRepository repo;

    @Override
    public BankReceiveOtpDTO checkBankReceiveOtp(String userId, String bankId, String otp) {
        return repo.checkBankReceiveOtp(userId, bankId, otp);
    }
}
