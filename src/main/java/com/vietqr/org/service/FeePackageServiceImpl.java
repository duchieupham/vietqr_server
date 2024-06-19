package com.vietqr.org.service;

import com.vietqr.org.dto.IFeePackageDTO;
import com.vietqr.org.repository.FeePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeePackageServiceImpl implements FeePackageService {

    @Autowired
    private FeePackageRepository repo;

    @Override
    public List<IFeePackageDTO> getListFeePackageByName(String value, int offset, int size) {
        return repo.getFeePackageByName(value, offset, size);
    }

    @Override
    public List<IFeePackageDTO> getListFeePackageByFee(String value, int offset, int size) {
        return repo.getListFeePackageByFee(value, offset, size);
    }

    @Override
    public int countFeePackageByName(String value) {
        return repo.countFeePackageByName(value);
    }

    @Override
    public int countFeePackageByFee(String value) {
        return repo.countFeePackageByFee(value);
    }
}
