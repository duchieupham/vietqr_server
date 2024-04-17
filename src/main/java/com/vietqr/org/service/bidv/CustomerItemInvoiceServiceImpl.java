package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerItemInvoiceDataDTO;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;
import com.vietqr.org.repository.CustomerItemInvoiceRepository;

@Service
public class CustomerItemInvoiceServiceImpl implements CustomerItemInvoiceService {

    @Autowired
    CustomerItemInvoiceRepository repo;

    @Override
    public int insert(CustomerItemInvoiceEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<CustomerItemInvoiceDataDTO> getCustomerInvoiceItemByBillId(String billId) {
        return repo.getCustomerInvoiceItemByBillId(billId);
    }

    @Override
    public void removeInvocieItemsByBillId(String billId) {
        repo.removeInvocieItemsByBillId(billId);
    }

}
