package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.PaymentFeeAccBankEntity;

@Service
public interface PaymentFeeAccBankService {

    public int insert(PaymentFeeAccBankEntity entity);

    public PaymentFeeAccBankEntity checkExistedRecord(String bankId, String accBankFeeId, String month);
}
