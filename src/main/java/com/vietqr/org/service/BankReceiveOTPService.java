package com.vietqr.org.service;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import org.springframework.stereotype.Service;

@Service
public interface BankReceiveOTPService {
    BankReceiveOtpDTO checkBankReceiveOtp(String userId, String bankId, String otp);
}
