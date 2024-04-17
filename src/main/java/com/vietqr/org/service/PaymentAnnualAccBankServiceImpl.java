package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AnnualFeeItemDTO;
import com.vietqr.org.entity.PaymentAnnualAccBankEntity;
import com.vietqr.org.repository.PaymentAnnualAccBankRepository;

@Service
public class PaymentAnnualAccBankServiceImpl implements PaymentAnnualAccBankService {

    @Autowired
    PaymentAnnualAccBankRepository repo;

    @Override
    public int insert(PaymentAnnualAccBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<AnnualFeeItemDTO> getAnnualFeesByBankId(String bankId) {
        return repo.getAnnualFeesByBankId(bankId);
    }

}
