package com.vietqr.org.service;

import com.vietqr.org.dto.ITrAnnualFeeDTO;
import com.vietqr.org.repository.TrAnnualFeePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrAnnualFeePackageServiceImpl implements TrAnnualFeePackageService {

    @Autowired
    private TrAnnualFeePackageRepository repo;
    @Override
    public ITrAnnualFeeDTO getFeeById(String id) {
        return repo.getFeeById(id);
    }

    @Override
    public List<ITrAnnualFeeDTO> getAllFee() {
        return repo.getAllFee();
    }

    @Override
    public Long getTotalAmount(String feeId) {
        return repo.getTotalAmount(feeId);
    }
}
