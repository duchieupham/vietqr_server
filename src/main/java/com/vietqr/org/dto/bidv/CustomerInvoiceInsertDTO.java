package com.vietqr.org.dto.bidv;

import java.io.Serializable;
import java.util.List;

public class CustomerInvoiceInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String customerId;
    private List<InvoiceItemDTO> items;

    public CustomerInvoiceInsertDTO() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }

    public CustomerInvoiceInsertDTO(String name, String customerId, List<InvoiceItemDTO> items) {
        this.name = name;
        this.customerId = customerId;
        this.items = items;
    }

    public static class InvoiceItemDTO implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;

        private String name;
        private String description;
        private Long quantity;
        private Long amount;

        public InvoiceItemDTO() {
            super();
        }

        public InvoiceItemDTO(String name, String description, Long quantity, Long amount) {
            this.name = name;
            this.description = description;
            this.quantity = quantity;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

    }
}
