package com.vietqr.org.entity.bidv;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerInvoiceTransaction")
public class CustomerInvoiceTransactionEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "trans_id")
    private String trans_id;

    @Column(name = "trans_date")
    private String trans_date;

    @Column(name = "customer_id")
    private String customer_id;

    @Column(name = "service_id")
    private String service_id;

    @Column(name = "bill_id")
    private String bill_id;

    @Column(name = "amount")
    private String amount;

    @Column(name = "checksum")
    private String checksum;

    public CustomerInvoiceTransactionEntity() {
        super();
    }

    public CustomerInvoiceTransactionEntity(String id, String trans_id, String trans_date, String customer_id,
            String service_id, String bill_id, String amount, String checksum) {
        this.id = id;
        this.trans_id = trans_id;
        this.trans_date = trans_date;
        this.customer_id = customer_id;
        this.service_id = service_id;
        this.bill_id = bill_id;
        this.amount = amount;
        this.checksum = checksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
