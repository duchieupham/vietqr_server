package com.vietqr.org.service;

import com.vietqr.org.dto.InvoiceRequestPaymentDTO;
import com.vietqr.org.entity.InvoiceTransactionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceTransactionService {
    InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds, String bankIdRecharge);

    InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds);

    void insert(InvoiceTransactionEntity entity);

    InvoiceTransactionEntity getInvoiceTransactionByRefId(String refId);

    List<InvoiceTransactionEntity> getInvoiceTransactionsByRefId(String transactionId);
    void update(InvoiceTransactionEntity invoiceTransactionEntity);
}
