package com.vietqr.org.service;

import com.vietqr.org.dto.IFeePackageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeePackageService {

    List<IFeePackageDTO> getListFeePackageByName(String value, int offset, int size);
    List<IFeePackageDTO> getListFeePackageByFee(String value, int offset, int size);
    int countFeePackageByName(String value);
    int countFeePackageByFee(String value);

}
