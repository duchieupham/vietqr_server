package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerItemInvoiceDataDTO;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;

@Service
public interface CustomerItemInvoiceService {

    public int insert(CustomerItemInvoiceEntity entity);

    public List<CustomerItemInvoiceDataDTO> getCustomerInvoiceItemByBillId(String billId);

    public void removeInvocieItemsByBillId(String billId);
}
