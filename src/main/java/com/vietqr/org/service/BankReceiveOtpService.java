package com.vietqr.org.service;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import com.vietqr.org.entity.BankReceiveOtpEntity;
import org.springframework.stereotype.Service;

@Service
public interface BankReceiveOtpService {
    BankReceiveOtpDTO checkBankReceiveOtp(String userId, String bankId, String otp, String keyActive);

    int insert(BankReceiveOtpEntity entity);

    BankReceiveOtpEntity getBankReceiveOtpByKey(String keyActive, String bankId, String userId);

    void updateStatusBankReceiveOtp(String id, int status);
}
