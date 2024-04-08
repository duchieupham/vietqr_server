package com.vietqr.org.service;

import com.vietqr.org.dto.TrAnnualFeeDTO;
import org.springframework.stereotype.Service;

@Service
public interface TrAnnualFeePackageService {
    TrAnnualFeeDTO getFeeById(String id);
}
