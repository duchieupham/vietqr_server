package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class CustomerInvoiceRequestBankDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String customer_id;
    private String service_id;
    private String checksum;

    public CustomerInvoiceRequestBankDTO() {
        super();
    }

    public CustomerInvoiceRequestBankDTO(String customer_id, String service_id, String checksum) {
        this.customer_id = customer_id;
        this.service_id = service_id;
        this.checksum = checksum;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
