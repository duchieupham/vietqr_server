package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.repository.CustomerInvoiceRepository;

@Service
public class CustomerInvoiceServiceImpl implements CustomerInvoiceService {

    @Autowired
    CustomerInvoiceRepository repo;

    @Override
    public int insert(CustomerInvoiceEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedBillId(String billId) {
        return repo.checkExistedBillId(billId);
    }

    @Override
    public List<CustomerInvoiceDataDTO> getCustomerInvoiceAllStatus(String customerId, int offset) {
        return repo.getCustomerInvoiceAllStatus(customerId, offset);
    }

    @Override
    public CustomerInvoiceDataDTO getCustomerInvoiceByBillId(String billId) {
        return repo.getCustomerInvoiceByBillId(billId);
    }

    @Override
    public void removeInvocieByBillId(String billId) {
        repo.removeInvocieByBillId(billId);
    }

    @Override
    public CustomerInvoiceInfoDataDTO getCustomerInvoiceInfo(String customerId) {
        return repo.getCustomerInvoiceInfo(customerId);
    }

//    @Override
//    public List<CustomerInvoiceInfoDataDTO> getCustomerInvoiceInfos(String customerId) {
//        return repo.getCustomerInvoiceInfos(customerId);
//    }

    @Override
    public void updateCustomerVaInvoice(int status, Long timePaid, String billId) {
        repo.updateCustomerVaInvoice(status, timePaid, billId);
    }

    @Override
    public String getCustomerIdByBillId(String billId) {
        return repo.getCustomerIdByBillId(billId);
    }

    @Override
    public void updateInquiredInvoiceByBillId(int inquired, String billId) {
        repo.updateInquiredInvoiceByBillId(inquired, billId);
    }

}
