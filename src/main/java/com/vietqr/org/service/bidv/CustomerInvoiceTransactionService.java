package com.vietqr.org.service.bidv;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.bidv.CustomerInvoiceTransactionEntity;

@Service
public interface CustomerInvoiceTransactionService {

    public int insert(CustomerInvoiceTransactionEntity entity);
}
