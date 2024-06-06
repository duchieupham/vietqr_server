package com.vietqr.org.service.bidv;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;

@Service
public interface CustomerInvoiceService {

    public int insert(CustomerInvoiceEntity entity);

    public String checkExistedBillId(String billId);

    public List<CustomerInvoiceDataDTO> getCustomerInvoiceAllStatus(String customerId, int offset);

    public CustomerInvoiceDataDTO getCustomerInvoiceByBillId(String billId);

    public void updateInquiredInvoiceByBillId(int inquired, String billId);

    public void removeInvocieByBillId(String billId);

    public CustomerInvoiceInfoDataDTO getCustomerInvoiceInfo(String customerId);
    public List<CustomerInvoiceInfoDataDTO> getCustomerInvoiceInfos(String customerId);

    public void updateCustomerVaInvoice(int status, Long timePaid, String billId);

    public String getCustomerIdByBillId(String billId);
}
