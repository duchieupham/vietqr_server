package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class CustomerInvoicePaymentRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String trans_id;
    private String trans_date;
    private String customer_id;
    private String service_id;
    private String bill_id;
    private String amount;
    private String additionalData1;
    private String additionalData2;
    private String checksum;

    public CustomerInvoicePaymentRequestDTO() {
        super();
    }

    public CustomerInvoicePaymentRequestDTO(String trans_id, String trans_date, String customer_id, String service_id,
            String bill_id,
            String amount, String checksum) {
        this.trans_id = trans_id;
        this.trans_date = trans_date;
        this.customer_id = customer_id;
        this.service_id = service_id;
        this.bill_id = bill_id;
        this.amount = amount;
        this.checksum = checksum;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
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

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getAdditionalData1() {
        return additionalData1;
    }

    public void setAdditionalData1(String additionalData1) {
        this.additionalData1 = additionalData1;
    }

    public String getAdditionalData2() {
        return additionalData2;
    }

    public void setAdditionalData2(String additionalData2) {
        this.additionalData2 = additionalData2;
    }

    @Override
    public String toString() {
        return "CustomerInvoicePaymentRequestDTO [trans_id=" + trans_id + ", trans_date=" + trans_date
                + ", customer_id=" + customer_id + ", service_id=" + service_id + ", bill_id=" + bill_id
                + ", amount=" + amount + ", additionalData1=" + additionalData1 + ", additionalData2=" + additionalData2
                + ", checksum=" + checksum+ "]";
    }
}
