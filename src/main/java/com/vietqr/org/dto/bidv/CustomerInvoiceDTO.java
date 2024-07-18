package com.vietqr.org.dto.bidv;

import java.io.Serializable;
import java.util.List;

public class CustomerInvoiceDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String result_code;
    private String result_desc;
//    private String service_id;
    private String customer_id;
    private String customer_name;
    private String customer_addr;
    private List<InvoiceDTO> data;

    public CustomerInvoiceDTO() {
        super();
    }

    public CustomerInvoiceDTO(String result_code, String result_desc, String service_id, String customer_id,
                              String customer_name, String customer_addr, List<InvoiceDTO> data) {
        this.result_code = result_code;
        this.result_desc = result_desc;
//        this.service_id = service_id;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_addr = customer_addr;
        this.data = data;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getResult_desc() {
        return result_desc;
    }

    public void setResult_desc(String result_desc) {
        this.result_desc = result_desc;
    }

//    public String getService_id() {
//        return service_id;
//    }
//
//    public void setService_id(String service_id) {
//        this.service_id = service_id;
//    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_addr() {
        return customer_addr;
    }

    public void setCustomer_addr(String customer_addr) {
        this.customer_addr = customer_addr;
    }

    public List<InvoiceDTO> getData() {
        return data;
    }

    public void setData(List<InvoiceDTO> data) {
        this.data = data;
    }

    public static class InvoiceDTO implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private int type;
        private Long amount;
        private String bill_id;

        public InvoiceDTO() {
            super();
        }

        public InvoiceDTO(int type, Long amount, String bill_id) {
            this.type = type;
            this.amount = amount;
            this.bill_id = bill_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getBill_id() {
            return bill_id;
        }

        public void setBill_id(String bill_id) {
            this.bill_id = bill_id;
        }

    }

}
