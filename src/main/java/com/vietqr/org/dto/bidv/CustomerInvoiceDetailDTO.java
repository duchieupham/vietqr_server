package com.vietqr.org.dto.bidv;

import java.io.Serializable;
import java.util.List;

public class CustomerInvoiceDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String billId;
    private Long amount;
    private int status;
    private int type;
    private String name;
    private Long timeCreated;
    private Long timePaid;
    private String bankAccount;
    private String userBankName;
    private String customerId;
    private List<CustomerInvoiceItemDetailDTO> items;

    public CustomerInvoiceDetailDTO() {
        super();
    }

    public CustomerInvoiceDetailDTO(String billId, Long amount, int status, int type, String name, Long timeCreated,
            Long timePaid, String bankAccount, String userBankName, String customerId,
            List<CustomerInvoiceItemDetailDTO> items) {
        this.billId = billId;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.name = name;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.customerId = customerId;
        this.items = items;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Long timePaid) {
        this.timePaid = timePaid;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CustomerInvoiceItemDetailDTO> getItems() {
        return items;
    }

    public void setItems(List<CustomerInvoiceItemDetailDTO> items) {
        this.items = items;
    }

    public static class CustomerInvoiceItemDetailDTO implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private String id;
        private Long amount;
        private String billId;
        private String description;
        private String name;
        private Long quantity;
        private Long totalAmount;

        public CustomerInvoiceItemDetailDTO() {
            super();
        }

        public CustomerInvoiceItemDetailDTO(String id, Long amount, String billId, String description, String name,
                Long quantity, Long totalAmount) {
            this.id = id;
            this.amount = amount;
            this.billId = billId;
            this.description = description;
            this.name = name;
            this.quantity = quantity;
            this.totalAmount = totalAmount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getBillId() {
            return billId;
        }

        public void setBillId(String billId) {
            this.billId = billId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
        }

    }
}
