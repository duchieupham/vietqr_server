package com.vietqr.org.service;

import com.vietqr.org.entity.FeePackageEntity;
import org.springframework.stereotype.Service;

@Service
public interface FeePackageService {
    FeePackageEntity getFeePackageById(String feePackageId);
}
