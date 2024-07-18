package com.vietqr.org.service;

import com.vietqr.org.dto.IFeePackageDTO;
import com.vietqr.org.entity.FeePackageEntity;
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
    public IFeePackageDTO getFeePackageById(String id) {
        return repo.getFeePackageById(id);
    }

    @Override
    public int countFeePackageByName(String value) {
        return repo.countFeePackageByName(value);
    }

    @Override
    public int countFeePackageByFee(String value) {
        return repo.countFeePackageByFee(value);
    }

    @Override
    public int insertFeePackage(FeePackageEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public int checkFeePackageExist(String id) {
        return repo.checkFeePackageExist(id);
    }
}
