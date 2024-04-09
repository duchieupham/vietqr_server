package com.vietqr.org.service;

import com.vietqr.org.dto.TrAnnualFeeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrAnnualFeePackageService {
    TrAnnualFeeDTO getFeeById(String id);

    List<TrAnnualFeeDTO> getAllFee();
}
