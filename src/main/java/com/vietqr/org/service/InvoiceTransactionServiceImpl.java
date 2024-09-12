package com.vietqr.org.service;

import com.vietqr.org.dto.InvoiceRequestPaymentDTO;
import com.vietqr.org.entity.InvoiceTransactionEntity;
import com.vietqr.org.repository.InvoiceTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceTransactionServiceImpl implements InvoiceTransactionService {

    @Autowired
    private InvoiceTransactionRepository repo;

    @Override
    public InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds, String bankIdRecharge) {
        return repo.getInvoiceRequestPayment(invoiceId, itemIds, bankIdRecharge);
    }

    @Override
    public InvoiceRequestPaymentDTO getInvoiceRequestPayment(String invoiceId, String itemIds) {
        return repo.getInvoiceRequestPayment(invoiceId, itemIds);
    }

    @Override
    public void insert(InvoiceTransactionEntity entity) {
        repo.save(entity);
    }

    @Override
    public InvoiceTransactionEntity getInvoiceTransactionByRefId(String refId) {
        Optional<InvoiceTransactionEntity> transaction = repo.findByRefId(refId);
        return transaction.orElse(null);
    }

    @Override
    public List<InvoiceTransactionEntity> getInvoiceTransactionsByRefId(String transactionId) {
        return repo.findAllByRefId(transactionId);
    }

    @Override
    public void update(InvoiceTransactionEntity invoiceTransactionEntity) {
            repo.save(invoiceTransactionEntity);
    }
}
