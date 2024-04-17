package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.PaymentFeeAccBankEntity;
import com.vietqr.org.repository.PaymentFeeAccBankRepository;

@Service
public class PaymentFeeAccBankServiceImpl implements PaymentFeeAccBankService {

    @Autowired
    PaymentFeeAccBankRepository repo;

    @Override
    public int insert(PaymentFeeAccBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public PaymentFeeAccBankEntity checkExistedRecord(String bankId, String accBankFeeId, String month) {
        return repo.checkExistedRecord(bankId, accBankFeeId, month);
    }

}
