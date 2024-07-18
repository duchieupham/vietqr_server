package com.vietqr.org.service;

import com.vietqr.org.dto.ITrAnnualFeeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrAnnualFeePackageService {
    ITrAnnualFeeDTO getFeeById(String id);

    List<ITrAnnualFeeDTO> getAllFee();

    Long getTotalAmount(String feeId);
}
