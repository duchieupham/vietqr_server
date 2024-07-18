package com.vietqr.org.service;

import com.vietqr.org.dto.IFeePackageDTO;
import com.vietqr.org.entity.FeePackageEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeePackageService {

    List<IFeePackageDTO> getListFeePackageByName(String value, int offset, int size);
    List<IFeePackageDTO> getListFeePackageByFee(String value, int offset, int size);
    IFeePackageDTO getFeePackageById(String id);
    int countFeePackageByName(String value);
    int countFeePackageByFee(String value);
    public int insertFeePackage(FeePackageEntity entity);
    public int checkFeePackageExist(String id);

}
