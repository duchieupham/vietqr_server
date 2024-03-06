package com.vietqr.org.service.bidv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.bidv.CustomerInvoiceTransactionEntity;
import com.vietqr.org.repository.CustomerInvoiceTransactionRepository;

@Service
public class CustomerInvoiceTransactionServiceImpl implements CustomerInvoiceTransactionService {

    @Autowired
    CustomerInvoiceTransactionRepository repo;

    @Override
    public int insert(CustomerInvoiceTransactionEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
