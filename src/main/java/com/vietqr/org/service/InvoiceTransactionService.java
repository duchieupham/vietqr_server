package com.vietqr.org.service;

import com.vietqr.org.dto.InvoiceRequestPaymentDTO;
import com.vietqr.org.entity.InvoiceTransactionEntity;
import org.springframework.stereotype.Service;

@Service
public interface InvoiceTransactionService {
    InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds, String bankIdRecharge);

    void insert(InvoiceTransactionEntity entity);
}
