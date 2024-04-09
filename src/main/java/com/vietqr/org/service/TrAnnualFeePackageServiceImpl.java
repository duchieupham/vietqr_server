package com.vietqr.org.service;

import com.vietqr.org.dto.TrAnnualFeeDTO;
import com.vietqr.org.repository.TrAnnualFeePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrAnnualFeePackageServiceImpl implements TrAnnualFeePackageService {

    @Autowired
    private TrAnnualFeePackageRepository repo;
    @Override
    public TrAnnualFeeDTO getFeeById(String id) {
        return repo.getFeeById(id);
    }

    @Override
    public List<TrAnnualFeeDTO> getAllFee() {
        return repo.getAllFee();
    }
}
