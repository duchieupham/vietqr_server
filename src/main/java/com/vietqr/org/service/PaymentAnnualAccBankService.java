package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AnnualFeeItemDTO;
import com.vietqr.org.entity.PaymentAnnualAccBankEntity;

@Service
public interface PaymentAnnualAccBankService {
    public int insert(PaymentAnnualAccBankEntity entity);

    public List<AnnualFeeItemDTO> getAnnualFeesByBankId(String bankId);
}
